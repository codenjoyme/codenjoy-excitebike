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


import com.codenjoy.dojo.excitebike.model.items.Bike;
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.excitebike.TestUtils.parseBikes;
import static com.codenjoy.dojo.excitebike.TestUtils.printField;
import static com.codenjoy.dojo.excitebike.TestUtils.ticks;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameTest {

    private Excitebike game;
    private Bike bike;
    private Dice dice;
    private Player player;
    private GameSettings settings;

    @Before
    public void setup() {
        dice = mock(Dice.class);
        settings = new GameSettings();
    }

    private void init(String board, int xSize) {
        Bike bike = parseBikes(board, xSize).get(0);
        game = new Excitebike(new MapParser(board, xSize), dice, settings);
        player = new Player(mock(EventListener.class), settings);
        player.setHero(bike);
        game.newGame(player);
        this.bike = game.getAliveBikes().get(0);
    }

    @Test
    public void init__shouldFillFieldCorrectly() {
        // given
        String board = 
                "■■■■■" +
                " B ▼ " +
                "  >  " +
                " ▲ < " +
                "■■■■■";

        // when
        init(board, 5);

        // then
        aasertB("■■■■■" +
                " B ▼ " +
                "  >  " +
                " ▲ < " +
                "■■■■■");
    }

    @Test
    public void tick__shouldShiftTrack() {
        // given
        String board = 
                "■■■■■" +
                " B ▼ " +
                "  >  " +
                " ▲ < " +
                "■■■■■";
        init(board, 5);
        when(dice.next(20)).thenReturn(12);
        when(dice.next(5)).thenReturn(1);
        when(dice.next(3)).thenReturn(1);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                " B▼  " +
                " >  <" +
                "▲ <  " +
                "■■■■■");
    }

    @Test
    public void down__shouldMoveBikeToDown() {
        // given
        String board = 
                "■■■■■" +
                " B   " +
                "     " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.down();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " B   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void up__shouldMoveBikeToUp() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                " B   " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                " B   " +
                "     " +
                "     " +
                "■■■■■");
    }

    @Test
    public void crush__shouldFallBike() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.crush();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  b  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void tick__shouldIgnoreMovingAfterBikeIsFallen() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        bike.crush();
        game.tick();

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " b   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorTick1__shouldChangeBikeStateToAtInhibitor__atTheBeginning() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B<   " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "I    " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorTick2__shouldChangeBikeStateToNormal__atTheBeginning() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B<   " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "B    " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitor__shouldChangeBikeStateToAtInhibitor__atTheEnding() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B<" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "   I " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitor__shouldChangeBikeStateToAtInhibitor__afterBikeMoveUp() {
        // given
        String board = 
                "■■■■■" +
                "   < " +
                "  B  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                "  I  " +
                "     " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorTick1__shouldChangeStateToInhibited() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B< " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  I  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorTick2__shouldMoveInhibitedBikeOneStepBack() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B< " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " I   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorTick3__shouldChangeBikeStateToNormalAndMoveBackInhibitor() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B< " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "<B   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoInhibitorsTick1__shouldChangeStateToInhibited() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B<<" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  I< " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoInhibitorsTick2__shouldChangeKeepBikeStateInhibitedAndMoveItBack() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B<<" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " I<  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoInhibitorsTick3__shouldChangeKeepBikeStateInhibited() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B<<" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "<I   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoInhibitorsTick4__shouldChangeBikeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B<<" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 3);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "<B   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void accelerator__shouldChangeBikeStateToAtAccelerator__atTheBeginning() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>   " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "A    " +
                "     " +
                "■■■■■");
    }

    @Test
    public void accelerator__shouldChangeBikeStateToAtAccelerator__atTheEnding() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B>" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "   A " +
                "     " +
                "■■■■■");
    }

    @Test
    public void accelerator__shouldChangeBikeStateToAtAccelerator__afterBikeMoveUp() {
        // given
        String board = 
                "■■■■■" +
                "   > " +
                "  B  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                "  A  " +
                "     " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorTick1__shouldChangeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B> " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  A  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorTick2__shouldMoveAcceleratedBikeOneStepForwardAndChangeStateToNormalAndMoveAcceleratorBack() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B> " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " > B " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorTick3__shouldMoveBackAccelerator() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B> " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                ">  B " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsTick1__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "A>   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsTick2__shouldMoveBikeForwardAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                ">B   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsTick3__shouldChangeKeepBikeStateNormalButMoveItForward() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void obstacleTick1__shouldObstructBike() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "   o " +
                "     " +
                "■■■■■");
    }

    @Test
    public void obstacleTick2__shouldMoveObstructedBikeLeft() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  o  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void obstacleTick3__shouldMoveObstructedBikeLeft() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " o   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void obstacleTick4__shouldMoveObstructedBikeLeft() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 3);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "o    " +
                "     " +
                "■■■■■");
    }

    @Test
    public void obstacleTick5__shouldRespawnObstructedBike__ifItIsLastCell() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 4);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "     " +
                "B    " +
                "■■■■■");
    }

    @Test
    public void obstacleAndUpCommand__shouldMoveBikeUp() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        bike.up();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "   B " +
                "   | " +
                "     " +
                "■■■■■");
    }

    @Test
    public void tick2__shouldMoveObstructedBikeAndObstacleTogether() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "   B|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  o  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void tick1__shouldMakeBikeObstructed__IfBikeInteractedWithObstacle() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B|   " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "o    " +
                "     " +
                "■■■■■");
    }

    @Test
    public void tick2__shouldRespawnObstructedBike__ifItIsFirstColumn() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B|   " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "     " +
                "B    " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndInhibitorTick1__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B><" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  A< " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndInhibitorTick2__shouldChangeBikeStateToInhibited() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B><" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " >I  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndInhibitorTick3__shouldChangeBikeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B><" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "><B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndObstacleTick1__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B>|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  A| " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndObstacleTick2__shouldChangeBikeStateToFallen() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B>|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " >o  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndLineChangerUpTick1__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B>▲" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  A▲ " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndLineChangerUpTick2__shouldMoveBikeForwardAndUp() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B>▲" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "   B " +
                " >▲  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndLineChangerDownTick1__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B>▼" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  A▼ " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorAndLineChangerDownTick2__shouldMoveBikeForwardAndDown() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B>▼" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " >▼  " +
                "   B " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpTick1__shouldChangeBikeStateToAtLineChangerUp() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  U  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpTick2__shouldMoveBikeUpAndChangeStateBackToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "  B  " +
                " ▲   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownTick1__shouldChangeBikeStateToAtLineChangerDown() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  D  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownTick2__shouldMoveBikeDownAndChangeStateBackToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " ▼   " +
                "  B  " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpAndCommandUpTick1__shouldMoveBikeUpAndKeepStateNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                "  B  " +
                "  ▲  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpAndCommandUpTick2__shouldNotMoveBikeAndKeepState() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        bike.up();
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "  B  " +
                " ▲   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpTick2AndCommandDown__shouldNotMoveBikeAndChangeStateBackToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        bike.down();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " ▲B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownAndCommandDownTick1__shouldMoveBikeDownAndKeepStateNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.down();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  ▼  " +
                "  B  " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownAndCommandDownTick2__shouldNotMoveBikeAndChangeState() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        bike.down();
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " ▼   " +
                "  B  " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownTick2AndCommandUp__shouldNotMoveBikeAndChangeStateBackToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " ▼B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorAfterInhibitorTick1__shouldChangeStateToInhibited() {
        // given
        String board = 
                "■■■■■■■■" +
                "        " +
                "   B<  <" +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■";
        init(board, 8);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■" +
                "        " +
                "   I  < " +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■");
    }

    @Test
    public void inhibitorAfterInhibitorTick2__shouldMoveBikeBackAndKeepStateInhibited() {
        // given
        String board = 
                "■■■■■■■■" +
                "        " +
                "   B<  <" +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■";
        init(board, 8);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■" +
                "        " +
                "  I  <  " +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■");
    }

    @Test
    public void inhibitorAfterInhibitorTick3__shouldChangeStateToNormal() {
        // given
        String board = 
                "■■■■■■■■" +
                "        " +
                "   B<  <" +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■";
        init(board, 8);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■" +
                "        " +
                " <B <   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■");
    }

    @Test
    public void inhibitorAfterInhibitorTick4__shouldKeepStateNormal() {
        // given
        String board = 
                "■■■■■■■■" +
                "        " +
                "   B<  <" +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■";
        init(board, 8);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 3);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■" +
                "        " +
                "< B<    " +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■");
    }

    @Test
    public void inhibitorAfterInhibitorTick5__shouldChangeStateToInhibited() {
        // given
        String board = 
                "■■■■■■■■" +
                "        " +
                "   B<  <" +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■";
        init(board, 8);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 4);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■" +
                "        " +
                "  I     " +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■");
    }

    @Test
    public void inhibitorAfterInhibitorTick6__shouldMoveInhibitedBikeBack() {
        // given
        String board = 
                "■■■■■■■■" +
                "        " +
                "   B<  <" +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■";
        init(board, 8);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 5);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■" +
                "        " +
                " I      " +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■");
    }

    @Test
    public void inhibitorAfterInhibitorTick7__shouldChangeStateToNormal() {
        // given
        String board = 
                "■■■■■■■■" +
                "        " +
                "   B<  <" +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■";
        init(board, 8);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 6);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■" +
                "        " +
                "<B      " +
                "        " +
                "        " +
                "        " +
                "        " +
                "■■■■■■■■");
    }

    @Test
    public void twoLineChangersUpTick1__shouldChangeBikeStateToAtLineChangerUp() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲▲" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  U▲ " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoLineChangersUpTick2__shouldMoveBikeUpAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲▲" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "  B  " +
                " ▲▲  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpAndDownTick1__shouldChangeBikeStateToAtLineChangerUp() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲▼" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  U▼ " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpAndDownTick2__shouldMoveBikeUpAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲▼" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "  B  " +
                " ▲▼  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void commandUpToAcceleratorTick1__shouldMoveBikeUpAndChangeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "   > " +
                "  B| " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                "  A  " +
                "  |  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorWithSpaceAndObstacleTick1__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                " B> |" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " A | " +
                "     " +
                "■■■■■");
    }

    @Test
    public void acceleratorWithSpaceAndObstacleTick2__shouldMoveBikeForwardAndChangeStateToObstructed() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                " B> |" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "> o  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void shouldSpawnAcceleratorOnTheHighestLine() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "     " +
                "B    " +
                "■■■■■";
        init(board, 5);
        when(dice.next(20)).thenReturn(12);
        when(dice.next(5)).thenReturn(0);
        when(dice.next(3)).thenReturn(2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "    >" +
                "     " +
                "B    " +
                "■■■■■");
    }

    @Test
    public void left__shouldDoNothing() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.left();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void right__shouldDoNothing() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.right();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void act__shouldDoNothing() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.act();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownAndObstacleTick1__shouldChangeBikeStateToAtLineChangerDown() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  D| " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownAndObstacleTick2__shouldMoveBikeDownAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " ▼|  " +
                "  B  " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpAndObstacleTick1__shouldChangeBikeStateToAtLineChangerUp() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  U| " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpAndObstacleTick2__shouldMoveBikeUpAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲|" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "  B  " +
                " ▲|  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownAndAcceleratorTick1__shouldChangeBikeStateToAtLineChangerDown() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼>" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  D> " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownAndAcceleratorTick2__shouldMoveBikeDownAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼>" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " ▼>  " +
                "  B  " +
                "■■■■■");
    }

    @Test
    public void lineChangerDownAndAcceleratorTick3__shouldDoNothing() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼>" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "▼>   " +
                "  B  " +
                "■■■■■");
    }

    @Test
    public void inhibitorAndAcceleratorTick1__shouldChangeStateToInhibited() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B<>" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "  I> " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorAndAcceleratorTick2__shouldMoveBikeBack() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B<>" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " I>  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorAndAcceleratorTick3__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B<>" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "<A   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void inhibitorAndAcceleratorTick4__shouldChangeMoveBikeForwardAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B<>" +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 3);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "> B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpAndObstacleUpperTick1__shouldChangeBikeStateToAtLineChangerUp() {
        // given
        String board = 
                "■■■■■" +
                "    |" +
                "  B▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "   | " +
                "  U  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void lineChangerUpAndObstacleUpperTick2__shouldMoveBikeUpAndChangeStateToFallenAtObstacle() {
        // given
        String board = 
                "■■■■■" +
                "    |" +
                "  B▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "  o  " +
                " ▲   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsAndLineChangerUpTick1__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "A>▲  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsAndLineChangerUpTick2__shouldChangeBikeStateToAtLineChangerUp() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                ">U   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsAndLineChangerUpTick3__shouldMoveBikeUpAndRightAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "  B  " +
                "▲    " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsAndLineChangerDownTick1__shouldChangeBikeStateToAccelerated() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "A>▼  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsAndLineChangerDownTick2__shouldChangeBikeStateToAtLineChangerDown() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                ">D   " +
                "     " +
                "■■■■■");
    }

    @Test
    public void twoAcceleratorsAndLineChangerDownTick3__shouldMoveBikeDownAndRightAndChangeStateToNormal() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "B>>▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                "▼    " +
                "  B  " +
                "■■■■■");
    }

    @Test
    public void tick__shouldMoveBikeUp_ifBikeCrossRiseOfSpringboard() {
        // given
        String board = 
                "■■■╔═" +
                "   ˊ═" +
                "   ˊ═" +
                "  B╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■╔═  " +
                " ˊ═  " +
                " ˊB  " +
                " ╚ˊ  " +
                "■■■■■");
    }

    @Test
    public void tick__shouldMoveBikeUp_ifBikeCrossRiseOfSpringboard2() {
        // given
        String board = 
                "■■■╔═" +
                "  Bˊ═" +
                "   ˊ═" +
                "   ╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■╔B  " +
                " ˊ═  " +
                " ˊ═  " +
                " ╚ˊ  " +
                "■■■■■");
    }

    @Test
    public void tick__shouldMoveBikeUp_ifBikeCrossRiseOfSpringboard3() {
        // given
        String board = 
                "■■■╔═" +
                "   ˊ═" +
                "  Bˊ═" +
                "   ╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■╔═  " +
                " ˊB  " +
                " ˊ═  " +
                " ╚ˊ  " +
                "■■■■■");
    }

    @Test
    public void tick__shouldSetStateBikeInFlight() {
        // given
        String board = 
                "■■■╔═" +
                "  ˊ═ " +
                "  ˊB " +
                "  ╚ˊˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        bike.down();
        game.tick();

        // then
        aasertB("■■╔═ " +
                " ˊ═  " +
                " ˊ   " +
                " ╚ˊF " +
                "■■■■■");
    }

    @Test
    public void tick__shouldCrushBikeAtFence_ifBikeWasInFlight() {
        // given
        String board = 
                "■■■╔═" +
                "  ˊ═ " +
                "  ˊB " +
                "  ╚ˊˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        bike.down();
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■╔═  " +
                "ˊ═   " +
                "ˊ    " +
                "╚ˊˊ  " +
                "■■■f■");
    }


    @Test
    public void tick__shouldCrushBikeAtFence_ifBikeWasInFlight2() {
        // given
        String board = 
                "■■■╔═" +
                "  ˊ═ " +
                "  ˊ  " +
                " B╚ˊˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);
        bike.down();
        game.tick();

        // when
        game.tick();

        // then
        aasertB("═■■  " +
                "     " +
                "     " +
                "ˊ    " +
                "■f■■■");
    }

    @Test
    public void tick__shouldCrushBike_ifBikeMoveUpOnSpringboardFence() {
        // given
        String board = 
                "■■■╔═" +
                "  Bˊ═" +
                "   ˊ═" +
                "   ╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 2);
        bike.up();

        // when
        game.tick();

        // then
        aasertB("╔═■  " +
                "ˊ═   " +
                "ˊ═   " +
                "╚ˊ   " +
                "■■■■■");
        assertThat(bike.isAlive(), is(false));
    }

    @Test
    public void tick__shouldMoveBikeDown_ifBikeCrossDescentOfSpringboard() {
        // given
        String board = 
                "■■■═╗" +
                "   ═ˋ" +
                " B  ˋ" +
                "   ˋ╝" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();
        game.tick();
        game.tick();

        // when
        game.tick();

        // then
        aasertB("╗■■  " +
                "ˋ    " +
                "ˋ    " +
                "╝B   " +
                "■■■■■");
    }

    @Test
    public void tick__shouldMoveBikeDown_ifBikeCrossDescentOfSpringboard2() {
        // given
        String board = 
                "■■■═╗" +
                " B  ˋ" +
                "   ═ˋ" +
                "   ˋ╝" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();
        game.tick();
        game.tick();

        // when
        game.tick();

        // then
        aasertB("╗■■  " +
                "ˋ    " +
                "ˋB   " +
                "╝    " +
                "■■■■■");
    }

    @Test
    public void tick__shouldMoveBikeDown_ifBikeCrossDescentOfSpringboard3() {
        // given
        String board = 
                "■■■═╗" +
                "   ═ˋ" +
                " B  ˋ" +
                "   ˋ╝" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();
        game.tick();

        // when
        game.tick();

        // then
        aasertB("═╗■  " +
                "═ˋ   " +
                " ˋ   " +
                "ˋS   " +
                "■■■■■");
    }

    @Test
    public void tick__shouldMoveBikeDown_ifBikeCrossDescentOfSpringboard4() {
        // given
        String board = 
                "■■■═╗" +
                " B  ˋ" +
                "   ═ˋ" +
                "   ˋ╝" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();
        game.tick();

        // when
        game.tick();

        // then
        aasertB("═╗■  " +
                " ˋ   " +
                "═R   " +
                "ˋ╝   " +
                "■■■■■");
    }

    @Test
    public void tick__shouldNotMoveBikeDown_ifBikeCommandDownBeforeSpringboardRise() {
        // given
        String board = 
                "■■■╔═" +
                "B  ˊ═" +
                "   ˊ═" +
                "   ╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();
        game.tick();
        bike.down();
        game.tick();

        // then
        aasertB("╔═■  " +
                "L═   " +
                "ˊ═   " +
                "╚ˊ   " +
                "■■■■■");
    }

    @Test
    public void tick__shouldNotMoveBikeDown_ifBikeCommandDownBeforeSpringboardRise2() {
        // given
        String board = 
                "■■■╔═" +
                "   ˊ═" +
                "   ˊ═" +
                "B  ╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();
        game.tick();
        bike.down();
        game.tick();

        // then
        aasertB("╔═■  " +
                "ˊ═   " +
                "ˊ═   " +
                "Mˊ   " +
                "■■■■■");
    }

    @Test
    public void tick__shouldNotMoveBikeUp_ifBikeCommandUpAtSpringboardRise() {
        // given
        String board = 
                "■■■╔═" +
                "   ˊ═" +
                "   ˊ═" +
                "B  ╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();
        game.tick();
        game.tick();
        bike.up();
        game.tick();

        // then
        aasertB("═■■  " +
                "B    " +
                "═    " +
                "ˊ    " +
                "■■■■■");
    }

    @Test
    public void tick__shouldNotMoveBikeDownAndSetInFlightState_ifBikeCommandDownAtSpringboardRise() {
        // given
        String board = 
                "■■■╔═" +
                "   ˊ═" +
                "   ˊ═" +
                "B  ╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();
        game.tick();
        game.tick();
        bike.down();
        game.tick();

        // then
        aasertB("═■■  " +
                "═    " +
                "═    " +
                "F    " +
                "■■■■■");
    }

    @Test
    public void tick__shouldNotMoveBikeUpAndCrushIt_ifBikeCommandUpAtSpringboardRise() {
        // given
        String board = 
                "■■■╔═" +
                "B  ˊ═" +
                "   ˊ═" +
                "   ╚ˊ" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();
        game.tick();
        game.tick();
        bike.up();
        game.tick();

        // then
        aasertB("═■■  " +
                "═    " +
                "═    " +
                "ˊ    " +
                "■■■■■");
        assertThat(player.isAlive(), is(false));
    }

    @Test
    public void tick__shouldNotMoveBikeDown_ifBikeCommandDownBeforeSpringboardDescent() {
        // given
        String board = 
                "■■=═╗" +
                " B==ˋ" +
                "  =═ˋ" +
                "   ˋ╝" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();
        game.tick();
        bike.down();
        game.tick();

        // then
        aasertB("═╗   " +
                " ˋ   " +
                "═R   " +
                "ˋ╝   " +
                "■■■■■");
    }

    @Test
    public void tick__shouldMoveBikeDownAndCrushIt_ifBikeCommandDownAtSpringboardDescent() {
        // given
        String board = 
                "■■=═╗" +
                "  ==ˋ" +
                " B=═ˋ" +
                "   ˋ╝" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();
        game.tick();
        game.tick();
        bike.down();
        game.tick();

        // then
        aasertB("╗■   " +
                "ˋ    " +
                "ˋ    " +
                "╝    " +
                "■f■■■");
    }

    @Test
    public void tick__shouldMoveBikeUpAndCrushIt_ifBikeCommandUpAtSpringboardDescent() {
        // given
        String board = 
                "■■■╔╗" +
                "B  ˊˋ" +
                "   ˊˋ" +
                "   ╚╝" +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        bike.up();
        game.tick();

        // then
        aasertB("f■■  " +
                "     " +
                "     " +
                "     " +
                "■■■■■");
        assertThat(player.isAlive(), is(false));
    }

    @Test
    public void tick2__shouldSpawnStraightObstaclesLineWithOneExit__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(0);
        when(dice.next(3)).thenReturn(0);
        when(dice.next(10)).thenReturn(9, 7, 8, 6, 4);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "           |" +
                "           |" +
                "           |" +
                "           |" +
                "           |" +
                "B           " +
                "           |" +
                "           |" +
                "           |" +
                "           |" +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick1__shouldSpawnEmptyLine__ifDiceReturnsAccordingForObstacleLadderUpNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick2__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "           |" +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick3__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "           |" +
                "          | " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick4__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 3);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "           |" +
                "          | " +
                "         |  " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick5__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 4);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "           |" +
                "          | " +
                "         |  " +
                "        |   " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick6__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 5);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B          |" +
                "          | " +
                "         |  " +
                "        |   " +
                "       |    " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick7__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 6);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "           |" +
                "B         | " +
                "         |  " +
                "        |   " +
                "       |    " +
                "      |     " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick8__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 7);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "           |" +
                "          | " +
                "B        |  " +
                "        |   " +
                "       |    " +
                "      |     " +
                "     |      " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick9__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 8);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "           |" +
                "          | " +
                "         |  " +
                "B       |   " +
                "       |    " +
                "      |     " +
                "     |      " +
                "    |       " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick10__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 9);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "           |" +
                "          | " +
                "         |  " +
                "        |   " +
                "B      |    " +
                "      |     " +
                "     |      " +
                "    |       " +
                "   |        " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick11__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 10);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "          | " +
                "         |  " +
                "        |   " +
                "       |    " +
                "B     |     " +
                "     |      " +
                "    |       " +
                "   |        " +
                "  |         " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick12__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 11);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "         |  " +
                "        |   " +
                "       |    " +
                "      |     " +
                "B    |      " +
                "    |       " +
                "   |        " +
                "  |         " +
                " |          " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick13__shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 12);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "        |   " +
                "       |    " +
                "      |     " +
                "     |      " +
                "B   |       " +
                "   |        " +
                "  |         " +
                " |          " +
                "|           " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick1__shouldSpawnEmptyLine__ifDiceReturnsAccordingToObstacleLadderDownNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick2__shouldSpawnEmptyLineWhichIsExit__ifDiceReturnsAccordingToObstacleLadderDownNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        game.tick();

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick3__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 2);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "           |" +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick4__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 3);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "          | " +
                "           |" +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick5__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 4);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "         |  " +
                "          | " +
                "           |" +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick6__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 5);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "        |   " +
                "         |  " +
                "          | " +
                "           |" +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick7__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 6);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "       |    " +
                "        |   " +
                "         |  " +
                "          | " +
                "B          |" +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick8__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 7);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "      |     " +
                "       |    " +
                "        |   " +
                "         |  " +
                "B         | " +
                "           |" +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick9__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 8);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "     |      " +
                "      |     " +
                "       |    " +
                "        |   " +
                "B        |  " +
                "          | " +
                "           |" +
                "            " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick10__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 9);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "    |       " +
                "     |      " +
                "      |     " +
                "       |    " +
                "B       |   " +
                "         |  " +
                "          | " +
                "           |" +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick11__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 10);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "   |        " +
                "    |       " +
                "     |      " +
                "      |     " +
                "B      |    " +
                "        |   " +
                "         |  " +
                "          | " +
                "           |" +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick12__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 11);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                "  |         " +
                "   |        " +
                "    |       " +
                "     |      " +
                "B     |     " +
                "       |    " +
                "        |   " +
                "         |  " +
                "          | " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick13__shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(9);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 12);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "            " +
                " |          " +
                "  |         " +
                "   |        " +
                "    |       " +
                "B    |      " +
                "      |     " +
                "       |    " +
                "        |   " +
                "         |  " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick6__shouldSpawnLadderUpAndDownObstaclesLineWithOneExitAndWidthFour__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(1);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(1);
        when(dice.next(10)).thenReturn(3);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 5);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "          | " +
                "         |  " +
                "        |   " +
                "       |    " +
                "        |   " +
                "B        |  " +
                "          | " +
                "         |  " +
                "        |   " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void tick6__shouldSpawnLadderDownAndUpObstaclesLineWithOneExitAndWidthFour__ifDiceReturnsAccordingNumbers() {
        // given
        String board = 
                "■■■■■■■■■■■■" +
                "            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "B           " +
                "            " +
                "            " +
                "            " +
                "            " +
                "■■■■■■■■■■■■";
        init(board, 12);
        when(dice.next(3)).thenReturn(2);
        when(dice.next(20)).thenReturn(18);
        when(dice.next(4)).thenReturn(2);
        when(dice.next(10)).thenReturn(3);
        when(dice.next(5)).thenReturn(0);
        ticks(game, 5);

        // when
        game.tick();

        // then
        aasertB("■■■■■■■■■■■■" +
                "       |    " +
                "        |   " +
                "         |  " +
                "          | " +
                "         |  " +
                "B       |   " +
                "       |    " +
                "        |   " +
                "         |  " +
                "            " +
                "■■■■■■■■■■■■");
    }

    @Test
    public void commandUpAtLineChangerDown__shouldNotMoveBike() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▼ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " ▼B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void commandDownAtLineChangerUp__shouldNotMoveBike() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "  B▲ " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        bike.down();
        game.tick();

        // then
        aasertB("■■■■■" +
                "     " +
                " ▲B  " +
                "     " +
                "■■■■■");
    }

    @Test
    public void commandDownAtLineChangerDown__shouldMoveBikeForTwoCells() {
        // given
        String board = 
                "■■■■■" +
                "  B▼ " +
                "     " +
                "     " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        bike.down();
        game.tick();

        // then
        aasertB("■■■■■" +
                " ▼   " +
                "     " +
                "  B  " +
                "■■■■■");
    }

    @Test
    public void commandUpAtLineChangerUp__shouldMoveBikeForTwoCells() {
        // given
        String board = 
                "■■■■■" +
                "     " +
                "     " +
                "  B▲ " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);
        game.tick();

        // when
        bike.up();
        game.tick();

        // then
        aasertB("■■■■■" +
                "  B  " +
                "     " +
                " ▲   " +
                "■■■■■");
    }

    @Test
    public void shouldInhibitBikeOnlyOnceAfterCrossingSpringboard() {
        // given
        String board = 
                "■■■■■■■■╔╗■■■" +
                "     B< ˊˋ  <" +
                "        ˊˋ   " +
                "        ˊˋ   " +
                "        ˊˋ   " +
                "        ˊˋ   " +
                "        ˊˋ   " +
                "        ˊˋ   " +
                "        ˊˋ   " +
                "        ˊˋ   " +
                "        ˊˋ   " +
                "        ╚╝   " +
                "■■■■■■■■■■■■■";
        init(board, 13);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        ticks(game, 10);

        // then
        aasertB("■■■■■■■■  ■■■" +
                "  <B         " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "■■■■■■■■■■■■■");
    }

    @Test
    public void shouldInhibitBikeOnlyOnceAfterCrossingSpringboard2() {
        // given
        String board = 
                "■■■■■■■╔═╗■■■" +
                "    B< ˊ═ˋ  <" +
                "       ˊ═ˋ   " +
                "       ˊ═ˋ   " +
                "       ˊ═ˋ   " +
                "       ˊ═ˋ   " +
                "       ˊ═ˋ   " +
                "       ˊ═ˋ   " +
                "       ˊ═ˋ   " +
                "       ˊ═ˋ   " +
                "       ˊ═ˋ   " +
                "       ╚═╝   " +
                "■■■■■■■■■■■■■";
        init(board, 13);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        ticks(game, 11);

        // then
        aasertB("■■■■■■■   ■■■" +
                " <B          " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "             " +
                "■■■■■■■■■■■■■");
    }

    public void aasertB(String expected) {
        assertThat(printField(game, player), is(TestUtils.injectN(expected)));
    }

    @Test
    public void tick__shouldNotMoveBikeDown_ifBikeJustSpawnedAtSpringboard() {
        // given
        String board = 
                "■■■╔════╗■" +
                "   ˊ════ˋ " +
                "   ˊ════ˋ " +
                "   ˊ════ˋ " +
                "   ˊ════ˋ " +
                "   ˊ════ˋ " +
                "   ˊ════ˋ " +
                "   ˊ════ˋ " +
                "B  ╚ˊˊˊˊ╝  " +
                "■■■■■■■■■■";
        init(board, 10);
        when(dice.next(anyInt())).thenReturn(5);
        ticks(game, 5);
        bike.crush();
        ticks(game, 2);

        // when
        player.getHero().down();
        game.tick();

        // then
        aasertB("╗■■      ■" +
                "ˋ         " +
                "ˋ         " +
                "ˋ         " +
                "ˋ         " +
                "ˋ         " +
                "ˋ         " +
                "ˋ         " +
                "S         " +
                "■■■■■■■■■■");
    }

    @Test
    public void bikeInFrontOfObstacleAtLine1__tick1__shouldBeCrushedAtObstacle() {
        // given
        String board = 
                "■■■■■" +
                " ||  " +
                " ||  " +
                "B||  " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "||   " +
                "||   " +
                "o|   " +
                "■■■■■");
    }

    @Test
    public void bikeInFrontOfObstacleAtLine1__tick2__shouldBeRespawnedAtFreeSpace() {
        // given
        String board = 
                "■■■■■" +
                " ||  " +
                " ||  " +
                "B||  " +
                "■■■■■";
        init(board, 5);
        when(dice.next(anyInt())).thenReturn(5);

        aasertB("■■■■■" +
                " ||  " +
                " ||  " +
                "B||  " +
                "■■■■■");

        game.tick();

        aasertB("■■■■■" +
                "||   " +
                "||   " +
                "o|   " +
                "■■■■■");

        // when
        game.tick();

        // then
        aasertB("■■■■■" +
                "|    " +
                "|B   " +
                "|    " +
                "■■■■■");
    }
}
