package com.codenjoy.dojo.excitebike.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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
import com.codenjoy.dojo.excitebike.model.items.*;
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldTest {

    private Field gameField;
    private MapParser mapParser;
    private Dice dice;
    private GameSettings settings;

    @Before
    public void init() {
        mapParser = mock(MapParser.class);
        when(mapParser.width()).thenReturn(5);
        when(mapParser.height()).thenReturn(5);
        dice = mock(Dice.class);
        settings = new TestGameSettings();
    }

    @Test
    public void isFence_shouldReturnTrue_IfYEqualsZero() {
        //given
        int x = 1, y = 0;
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        boolean result = gameField.isFence(x, y);

        //then
        assertThat(result, is(true));
    }

    @Test
    public void isFence_shouldReturnTrue_IfYEqualsMaxPossibleValue() {
        //given
        int x = 1, y = 2;
        when(mapParser.height()).thenReturn(3);
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        boolean result = gameField.isFence(x, y);

        //then
        assertThat(result, is(true));
    }

    @Test
    public void isFence_shouldReturnFalse_IfYIsNotZeroAndMaxPossibleValue() {
        //given
        int x = 1, y = 1;
        when(mapParser.height()).thenReturn(3);
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        boolean result = gameField.isFence(x, y);

        //then
        assertThat(result, is(false));
    }

    @Test
    public void isInhibitor_shouldReturnTrue() {
        //given
        int x = 1, y = 1;
        Inhibitor inhibitor = new Inhibitor(x, y);
        when(mapParser.inhibitors()).thenReturn(Collections.singletonList(inhibitor));
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        boolean result = gameField.isInhibitor(x, y);

        //then
        assertThat(result, is(true));
    }

    @Test
    public void isAccelerator_shouldReturnTrue() {
        //given
        int x = 1, y = 1;
        Accelerator accelerator = new Accelerator(x, y);
        when(mapParser.accelerators()).thenReturn(Collections.singletonList(accelerator));
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        boolean result = gameField.isAccelerator(x, y);

        //then
        assertThat(result, is(true));
    }

    @Test
    public void isObstacle_shouldReturnTrue() {
        //given
        int x = 1, y = 1;
        Obstacle obstacle = new Obstacle(x, y);
        when(mapParser.getObstacles()).thenReturn(Collections.singletonList(obstacle));
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        boolean result = gameField.isObstacle(x, y);

        //then
        assertThat(result, is(true));
    }

    @Test
    public void isUpLineChanger_shouldReturnTrue() {
        //given
        int x = 1, y = 1;
        LineChanger lineChanger = new LineChanger(x, y, true);
        when(mapParser.lineUp()).thenReturn(Collections.singletonList(lineChanger));
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        boolean result = gameField.isUpLineChanger(x, y);

        //then
        assertThat(result, is(true));
    }

    @Test
    public void isDownLineChanger_shouldReturnTrue() {
        //given
        int x = 1, y = 1;
        LineChanger lineChanger = new LineChanger(x, y, false);
        when(mapParser.lineDown()).thenReturn(Collections.singletonList(lineChanger));
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        boolean result = gameField.isDownLineChanger(x, y);

        //then
        assertThat(result, is(true));
    }

    @Test
    public void getEnemyBike_shouldReturnEmptyOptional_ifThereIsOnlyThisBikeAtGivenCoordinates() {
        //given
        gameField = new Excitebike(mapParser, dice, settings);
        Player player = new Player(mock(EventListener.class), settings);
        gameField.newGame(player);

        //when
        Optional<Bike> result = gameField.getEnemyBike(player.getHero().getX(), player.getHero().getY(), player);

        //then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void getEnemyBike_shouldReturnEmptyOptional_ifGivenPlayerIsNull() {
        //given
        gameField = new Excitebike(mapParser, dice, settings);
        Player player = new Player(mock(EventListener.class), settings);
        gameField.newGame(player);

        //when
        Optional<Bike> result = gameField.getEnemyBike(player.getHero().getX(), player.getHero().getY(), null);

        //then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void getEnemyBike_shouldReturnOptionalWithEnemyBike_ifThereIsOneAtGivenCoordinates() {
        //given
        gameField = new Excitebike(mapParser, dice, settings);
        int x = 1;
        int y = 1;
        Bike thisBike = new Bike(x, y);
        Bike enemyBike = new Bike(x, y);
        Player thisPlayer = mock(Player.class);
        when(thisPlayer.getHero()).thenReturn(thisBike);
        Player enemyPlayer = mock(Player.class);
        when(enemyPlayer.getHero()).thenReturn(enemyBike);
        gameField.newGame(thisPlayer);
        gameField.newGame(enemyPlayer);

        //when
        Optional<Bike> result = gameField.getEnemyBike(x, y, thisPlayer);

        //then
        assertThat(result.isPresent(), is(true));
    }

    @Test
    public void tick_shouldShiftAllShiftableElementsAndRemoveTheseAreOutOfBound() {
        //given
        Accelerator accelerator = new Accelerator(0, 1);
        Inhibitor inhibitor = new Inhibitor(1, 1);
        Obstacle obstacle = new Obstacle(2, 1);
        LineChanger upperLineChanger = new LineChanger(0, 2, true);
        LineChanger lowerLineChanger = new LineChanger(1, 2, false);

        when(mapParser.accelerators()).thenReturn(new ArrayList<>(Collections.singletonList(accelerator)));
        when(mapParser.inhibitors()).thenReturn(new ArrayList<>(Collections.singletonList(inhibitor)));
        when(mapParser.getObstacles()).thenReturn(new ArrayList<>(Collections.singletonList(obstacle)));
        when(mapParser.lineUp()).thenReturn(new ArrayList<>(Collections.singletonList(upperLineChanger)));
        when(mapParser.lineDown()).thenReturn(new ArrayList<>(Collections.singletonList(lowerLineChanger)));

        gameField = new Excitebike(mapParser, dice, settings);

        when(dice.next(anyInt())).thenReturn(5);

        //when
        gameField.tick();

        //then
        assertThat(accelerator.getX(), is(-1));
        assertThat(gameField.isAccelerator(accelerator.getX(), accelerator.getY()), is(false));
        assertThat(inhibitor.getX(), is(0));
        assertThat(obstacle.getX(), is(1));
        assertThat(upperLineChanger.getX(), is(-1));
        assertThat(gameField.isUpLineChanger(upperLineChanger.getX(), upperLineChanger.getY()), is(false));
        assertThat(lowerLineChanger.getX(), is(0));
    }

    @Test
    public void tick_shouldGenerateAccelerator() {
        //given
        int xSize = 5;
        int ySize = 5;
        int nonFenceElementOrdinal = 0;
        int nonFenceLaneNumber = 0;

        when(mapParser.width()).thenReturn(xSize);
        when(mapParser.height()).thenReturn(ySize);
        when(dice.next(anyInt())).thenReturn(12, nonFenceElementOrdinal, nonFenceLaneNumber);
        gameField = new Excitebike(mapParser, dice, settings);

        //when
        gameField.tick();

        //then
        assertThat(gameField.isAccelerator(xSize - 1, 1), is(true));
    }

    @Test
    public void getPlayerOfBike_shouldReturnNull_ifThereIsNoPlayerWithGivenBike() {
        //given
        gameField = new Excitebike(mapParser, dice, settings);
        Player player = new Player(mock(EventListener.class), settings);
        gameField.newGame(player);

        //when
        Player result = gameField.getPlayerOfBike(new Bike(2, 22));

        //then
        assertThat(result, nullValue());
    }

    @Test
    public void getPlayerOfBike_shouldReturnOptionalWithEnemyBike_ifThereIsOneAtGivenCoordinates() {
        //given
        gameField = new Excitebike(mapParser, dice, settings);
        int x = 1;
        int y = 1;
        Bike givenBike = new Bike(x, y);
        Player givenPlayer = mock(Player.class);
        when(givenPlayer.getHero()).thenReturn(givenBike);
        gameField.newGame(givenPlayer);
        //when
        Player result = gameField.getPlayerOfBike(givenBike);

        //then
        assertThat(result, is(givenPlayer));
    }

}
