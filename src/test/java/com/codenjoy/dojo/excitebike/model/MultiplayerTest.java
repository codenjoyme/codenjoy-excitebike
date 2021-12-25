package com.codenjoy.dojo.excitebike.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.excitebike.TestGameSettings;
import com.codenjoy.dojo.excitebike.model.items.Bike;
import com.codenjoy.dojo.excitebike.services.Event;
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import org.junit.Test;

import static com.codenjoy.dojo.excitebike.TestUtils.ticks;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class MultiplayerTest {

    private Game game1;
    private EventListener eventListenerSpy1 = spy(EventListener.class);
    private Game game2;
    private EventListener eventListenerSpy2 = spy(EventListener.class);
    private Game game3;
    private EventListener eventListenerSpy3 = spy(EventListener.class);
    private Dice dice;
    private Excitebike field;

    private void init() {
        MapParser mapParser = new MapParser(
                "■■■■■■■" +
                "       " +
                "       " +
                "       " +
                "       " +
                "       " +
                "■■■■■■■", 7);

        dice = mock(Dice.class);
        GameSettings settings = new TestGameSettings();
        field = new Excitebike(mapParser, dice, settings);
        PrinterFactory factory = new PrinterFactoryImpl();

        game1 = new Single(new Player(eventListenerSpy1, settings), factory);
        game1.on(field);

        game2 = new Single(new Player(eventListenerSpy2, settings), factory);
        game2.on(field);

        game3 = new Single(new Player(eventListenerSpy3, settings), factory);
        game3.on(field);

        game1.newGame();
        game2.newGame();
        game3.newGame();
    }

    @Test
    public void games_shouldInitializeCorrectly() {
        //given

        //when
        init();

        //then
        assertThat(game1.getBoardAsString(), is(
                "■■■■■■■\n" +
                "Ḃ      \n" +
                "       \n" +
                "Ḃ      \n" +
                "       \n" +
                "B      \n" +
                "■■■■■■■\n"));
        assertThat(game2.getBoardAsString(), is(
                "■■■■■■■\n" +
                "Ḃ      \n" +
                "       \n" +
                "B      \n" +
                "       \n" +
                "Ḃ      \n" +
                "■■■■■■■\n"));
        assertThat(game3.getBoardAsString(), is(
                "■■■■■■■\n" +
                "B      \n" +
                "       \n" +
                "Ḃ      \n" +
                "       \n" +
                "Ḃ      \n" +
                "■■■■■■■\n"));
    }

    @Test
    public void shouldJoystick() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);

        game3.getJoystick().up();
        game2.getJoystick().left();
        game1.getJoystick().down();

        //when
        field.tick();

        //then
        String expected =
                "ḟ■■■■■■\n" +
                "       \n" +
                "       \n" +
                "Ḃ      \n" +
                "       \n" +
                "       \n" +
                "f■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldRemove() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);

        //when
        game3.close();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "Ḃ      \n" +
                "       \n" +
                "B      \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldCrushEnemyBikeAfterClashAndGetEvents() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setY(bike2.getY() - 1);

        //when
        game1.getJoystick().up();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "Ḃ      \n" +
                "       \n" +
                "       \n" +
                "K      \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        assertThat(game2.isGameOver(), is(true));
        verify(eventListenerSpy1).event(Event.WIN);
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy2).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldCrushEnemyBikeAfterClash2AndGetScores() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setY(bike1.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().up();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "Ḱ      \n" +
                "B      \n" +
                "       \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        assertThat(game3.isGameOver(), is(true));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy3).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldCrushBikeAfterClashWithFenceAndGetScores() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);

        //when
        game1.getJoystick().down();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "Ḃ      \n" +
                "       \n" +
                "       \n" +
                "B      \n" +
                "       \n" +
                "ḟ■■■■■■\n";
        assertThat(game2.getBoardAsString(), is(expected));
        assertThat(game1.isGameOver(), is(true));
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy1).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
    }

    @Test
    public void shouldDoNothingAfterBikesClashEachOther_bike1() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setY(bike1.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "Ḃ      \n" +
                "Ḃ      \n" +
                "B      \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldDoNothingAfterBikesClashEachOther_bike1_tick2() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setY(bike1.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 1);
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //when
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "Ḃ      \n" +
                "Ḃ      \n" +
                "B      \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldDoNothingAfterBikesClashEachOther_bike2() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setY(bike1.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "Ḃ      \n" +
                "B      \n" +
                "Ḃ      \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game2.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldDoNothingAfterBikesClashEachOther_bike3() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setY(bike1.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "B      \n" +
                "Ḃ      \n" +
                "Ḃ      \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game3.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldMoveBikesInAnyOrderOfCall() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 2);

        //when
        game1.getJoystick().up();
        game2.getJoystick().up();
        game3.getJoystick().up();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "Ḃ      \n" +
                "Ḃ      \n" +
                "B      \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldIncrementYCoordinateForAllBikes() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 2);
        ticks(field, 7);

        //when
        field.tick();

        //then
        String expected =
                "╔═╗■■■■\n" +
                "ˊ═ˋ    \n" +
                "ˊḂˋ    \n" +
                "ˊḂˋ    \n" +
                "ˊBˋ    \n" +
                "╚ˊ╝    \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldNotIgnoreCommands_whenBikeBeforeTwoStepsFromSpringboardRise() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 2);
        ticks(field, 5);

        //when
        game1.getJoystick().down();
        game3.getJoystick().up();
        field.tick();

        //then
        String expected =
                "■■╔═╗■■\n" +
                "  ˊ═ˋ  \n" +
                " Ḃˊ═ˋ  \n" +
                "  ˊ═ˋ  \n" +
                " Ḃˊ═ˋ  \n" +
                "  ╚ˊ╝  \n" +
                "■f■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldNotSpawnBikeInFlight() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;
        init();
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 2);
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        ticks(field, 6);
        ((Bike) game1.getPlayer().getHero()).crush();
        ticks(field, 2);

        //when
        field.tick();

        //then
        String expected =
                "═╗■■■■■\n" +
                "═ˋ     \n" +
                "Ḃˋ     \n" +
                "═ˋ     \n" +
                "BŘ     \n" +
                "ˊ╝     \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldStartBikeFlightFromSpringboard_whenBikeTakeDownCommandOnSpringboardRise() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 2);
        ticks(field, 7);

        //when
        game1.getJoystick().down();
        field.tick();

        //then
        String expected =
                "╔═╗■■■■\n" +
                "ˊ═ˋ    \n" +
                "ˊḂˋ    \n" +
                "ˊḂˋ    \n" +
                "ˊ═ˋ    \n" +
                "╚F╝    \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldCrushBike_whenBikeHitOtherBikeAtTopOfSpringboardAndGetScores() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 2);
        ticks(field, 7);

        //when
        game1.getJoystick().up();
        field.tick();

        //then
        String expected =
                "╔═╗■■■■\n" +
                "ˊ═ˋ    \n" +
                "ˊḂˋ    \n" +
                "ˊKˋ    \n" +
                "ˊ═ˋ    \n" +
                "╚ˊ╝    \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        assertThat(game2.isGameOver(), is(true));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1).event(Event.WIN);
        verify(eventListenerSpy2).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldShiftCrushedBikeAtSpringboardTopAndGetScores() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 2);
        ticks(field, 7);

        //when
        game1.getJoystick().up();
        field.tick();
        field.tick();

        //then
        String expected =
                "═╗■■■■■\n" +
                "═ˋ     \n" +
                "═ˋ     \n" +
                "ḃŘ     \n" +
                "═R     \n" +
                "ˊ╝     \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        assertThat(game2.isGameOver(), is(true));
        verify(eventListenerSpy1).event(Event.WIN);
        verify(eventListenerSpy2).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldCrushOnFenceBikeAfterFlightFromSpringboardAndGetScores() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 2);
        ticks(field, 7);

        //when
        game1.getJoystick().down();
        field.tick();
        field.tick();

        //then
        String expected =
                "═╗■■■■■\n" +
                "═ˋ     \n" +
                "═ˋ     \n" +
                "═Ř     \n" +
                "═Ř     \n" +
                "ˊ╝     \n" +
                "■f■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldDecrementYCoordinateForAllBikes() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 4);
        ticks(field, 7);
        field.tick();

        //when
        field.tick();

        //then
        String expected =
                "═╗■■■■■\n" +
                "═ˋ     \n" +
                "═ˋ     \n" +
                "═R     \n" +
                "═Ř     \n" +
                "ˊŜ     \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeBeforeSpringboardDecent() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 4);
        ticks(field, 8);

        //when
        game2.getJoystick().up();
        game1.getJoystick().up();
        field.tick();

        //then
        String expected =
                "═╗■■■■■\n" +
                "═ˋ     \n" +
                "═ˋ     \n" +
                "═R     \n" +
                "═Ř     \n" +
                "ˊŜ     \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeBeforeSpringboardDecent2() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 4);
        ticks(field, 8);

        //when
        game3.getJoystick().up();
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "═╗■■■■■\n" +
                "═ˋ     \n" +
                "═ˋ     \n" +
                "═R     \n" +
                "═Ř     \n" +
                "ˊŜ     \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeBeforeSpringboardDecent3() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        ticks(field, 8);

        //when
        game3.getJoystick().up();
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "═╗■■■■■\n" +
                "═Ř     \n" +
                "═Ř     \n" +
                "═R     \n" +
                "═ˋ     \n" +
                "ˊ╝     \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldCrushBike_whenBikeAtHighestLineAndTakeUpCommandAfterSpringboardRiseAndGetScores() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        ticks(field, 7);

        //when
        game3.getJoystick().up();
        game2.getJoystick().up();
        game1.getJoystick().down();
        field.tick();

        //then
        String expected =
                "╔Ḃ╗■■■■\n" +
                "ˊ═ˋ    \n" +
                "ˊ═ˋ    \n" +
                "ˊBˋ    \n" +
                "ˊ═ˋ    \n" +
                "╚ˊ╝    \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        assertThat(game3.isGameOver(), is(true));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldCrushBike_whenBikeAtHighestLineAndTakeUpCommandAfterSpringboardDecentAndGetScores() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 1;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        ticks(field, 9);

        //when
        game3.getJoystick().up();
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "╗ḟ■■■■■\n" +
                "ˋ      \n" +
                "ˋḂ     \n" +
                "ˋB     \n" +
                "ˋ      \n" +
                "╝      \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeBeforeSpringboardDecent4() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 0;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 2);
        ticks(field, 7);

        //when
        game1.getJoystick().up();
        field.tick();

        //then
        String expected =
                "╔╗■■■■■\n" +
                "ˊˋ     \n" +
                "ˊˋ     \n" +
                "ˊŘ     \n" +
                "ˊŘ     \n" +
                "╚S     \n" +
                "■■■■■■■\n";

        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeBeforeSpringboardDecent5() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 0;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        bike3.setY(bike3.getY() - 2);
        ticks(field, 7);

        //when
        game1.getJoystick().up();
        game3.getJoystick().up();
        field.tick();

        //then
        String expected =
                "╔╗■■■■■\n" +
                "ˊˋ     \n" +
                "ˊˋ     \n" +
                "ˊŘ     \n" +
                "ˊŘ     \n" +
                "╚S     \n" +
                "■■■■■■■\n";

        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeBeforeSpringboardDecent6() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 0;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        ticks(field, 7);

        //when
        game1.getJoystick().up();
        game3.getJoystick().up();
        field.tick();

        //then
        String expected =
                "╔╗■■■■■\n" +
                "ˊŘ     \n" +
                "ˊŘ     \n" +
                "ˊR     \n" +
                "ˊˋ     \n" +
                "╚╝     \n" +
                "■■■■■■■\n";

        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldNotIgnoreAllCommands_whenBikeBeforeTwoStepsFromSpringboardRiseAndGetScores() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 0;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 1);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 1);
        bike2.setY(bike2.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX() + 1);
        ticks(field, 5);

        //when
        game1.getJoystick().up();
        game3.getJoystick().up();
        field.tick();

        //then
        String expected =
                "■ḟ╔╗■■■\n" +
                "  ˊˋ   \n" +
                " Kˊˋ   \n" +
                "  ˊˋ   \n" +
                "  ˊˋ   \n" +
                "  ╚╝   \n" +
                "■■■■■■■\n";

        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1).event(Event.WIN);
        verify(eventListenerSpy2).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void twoBikesBehindObstacleTick1_shouldBeSpawnedCorrectly() {
        //given
        init();
        game3.close();
        when(dice.next(20)).thenReturn(12);
        when(dice.next(5)).thenReturn(2, 2);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 4);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 5);

        //when
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "    BḂ|\n" +
                "       \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void twoBikesBehindObstacleTick2_shouldCrushEnemyBikeAtObstacle() {
        //given
        init();
        game3.close();
        when(dice.next(20)).thenReturn(12, 1);
        when(dice.next(5)).thenReturn(2, 2);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 4);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 5);
        field.tick();

        //when
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "    Bō \n" +
                "       \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void twoBikesBehindObstacleTick3_shouldCrushPlayerBikeAtEnemyAtObstacleAndGetScores() {
        //given
        init();
        game3.close();
        when(dice.next(20)).thenReturn(12, 1);
        when(dice.next(5)).thenReturn(2, 2);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX() + 4);
        bike1.setY(bike1.getY() + 2);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setX(bike2.getX() + 5);
        ticks(field, 2);

        //when
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "    o  \n" +
                "       \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeJustSpawnedBeforeSpringboardDecent() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 2;
        init();
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 2);
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        ticks(field, 8);
        ((Bike) game1.getPlayer().getHero()).crush();
        ((Bike) game2.getPlayer().getHero()).crush();
        ((Bike) game3.getPlayer().getHero()).crush();
        ticks(field, 2);

        //when
        game1.getJoystick().down();
        game3.getJoystick().up();
        field.tick();

        //then
        String expected =
                "╗■■■■■■\n" +
                "ˋ      \n" +
                "ˋ      \n" +
                "ŘḂ     \n" +
                "ˋ      \n" +
                "S      \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeJustSpawnedBeforeSpringboardDecent2() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 2;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 2);
        ticks(field, 8);
        ((Bike) game1.getPlayer().getHero()).crush();
        ((Bike) game2.getPlayer().getHero()).crush();
        ((Bike) game3.getPlayer().getHero()).crush();
        ticks(field, 2);

        //when
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "╗■■■■■■\n" +
                "ˋ      \n" +
                "ˋ      \n" +
                "Ř      \n" +
                "ˋḂ     \n" +
                "S      \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldIgnoreAllCommands_whenBikeJustSpawnedBeforeSpringboardDecent3() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 0;

        init();
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 2);
        ticks(field, 6);
        ((Bike) game1.getPlayer().getHero()).crush();
        ((Bike) game2.getPlayer().getHero()).crush();
        ((Bike) game3.getPlayer().getHero()).crush();
        ticks(field, 2);

        //when
        game2.getJoystick().down();
        field.tick();

        //then
        String expected =
                "╗■■■■■■\n" +
                "Ř      \n" +
                "ˋ      \n" +
                "Ř      \n" +
                "ˋ      \n" +
                "S      \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldNotCrushBike_whenBikeMovesToTopLineOfSpringboardAfterRespawn() {
        //given
        int springboardWeight = 17;
        int springboardTopSize = 7;
        init();
        Bike bike2 = (Bike) game2.getPlayer().getHero();
        bike2.setY(bike2.getY() - 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 2);
        when(dice.next(anyInt())).thenReturn(springboardWeight, springboardTopSize);
        ticks(field, 8);
        ((Bike) game1.getPlayer().getHero()).crush();
        ((Bike) game2.getPlayer().getHero()).crush();
        ((Bike) game3.getPlayer().getHero()).crush();
        ticks(field, 2);

        //when
        game3.getJoystick().up();
        field.tick();
        game3.getJoystick().up();
        field.tick();

        //then
        String expected =
                "════╗■■\n" +
                "═Ḃ══ˋ  \n" +
                "Ḃ═══ˋ  \n" +
                "════ˋ  \n" +
                "B═══ˋ  \n" +
                "ˊˊˊˊ╝  \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        verify(eventListenerSpy1, never()).event(Event.LOSE);
        verify(eventListenerSpy1, never()).event(Event.WIN);
        verify(eventListenerSpy2, never()).event(Event.LOSE);
        verify(eventListenerSpy2, never()).event(Event.WIN);
        verify(eventListenerSpy3, never()).event(Event.LOSE);
        verify(eventListenerSpy3, never()).event(Event.WIN);
    }

    @Test
    public void shouldMoveBike3DownFirstAndThenBike2Up_ifDiceReturnedRandomNumberLikeThatForTicking() {
        //given
        init();
        when(dice.next(3)).thenReturn(0);

        game3.getJoystick().down();
        game2.getJoystick().up();

        //when
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "K      \n" +
                "       \n" +
                "       \n" +
                "Ḃ      \n" +
                "■■■■■■■\n";
        assertThat(game2.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldMoveBike2UpFirstAndThenBike3Down_ifDiceReturnedRandomNumberLikeThatForTicking() {
        //given
        init();
        when(dice.next(3)).thenReturn(2);

        game3.getJoystick().down();
        game2.getJoystick().up();

        //when
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "Ḱ      \n" +
                "       \n" +
                "       \n" +
                "Ḃ      \n" +
                "■■■■■■■\n";
        assertThat(game2.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldKillLowerBikeByLowest_ifThereWerePairOfBikesWithLikewiseCommandsAndAnotherOneLowest_andDiceSaysPlayer3ShouldBeTicketFirstAndThenPlayer2AndThenPlayer1() {
        //given
        init();
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setY(bike1.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 1);
        when(dice.next(3)).thenReturn(-1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().up();
        game3.getJoystick().down();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "Ḃ      \n" +
                "K      \n" +
                "       \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        assertThat(game1.getPlayer().isAlive(), is(true));
        assertThat(game2.getPlayer().isAlive(), is(false));
        assertThat(game3.getPlayer().isAlive(), is(true));
    }

    @Test
    public void lowestBikeShouldKillLowerBikeAndThenShouldBeKilledByUpperBike_ifThereWerePairOfBikesWithLikewiseCommandsAndAnotherOneLowest_andDiceSaysPlayer1ShouldBeTicketFirstAndThenPlayer2AndThenPlayer3() {
        //given
        init();
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setY(bike1.getY() + 1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setY(bike3.getY() - 1);
        when(dice.next(3)).thenReturn(1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().up();
        game3.getJoystick().down();
        field.tick();

        //then
        String expected =
                "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "K      \n" +
                "       \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game3.getBoardAsString(), is(expected));
        assertThat(game1.getPlayer().isAlive(), is(false));
        assertThat(game2.getPlayer().isAlive(), is(false));
        assertThat(game3.getPlayer().isAlive(), is(true));
    }
}
