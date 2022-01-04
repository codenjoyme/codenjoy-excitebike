package com.codenjoy.dojo.excitebike.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
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
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.excitebike.TestUtils.*;
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
        settings = new TestGameSettings();
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
    public void init_shouldFillFieldCorrectly() {
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
    public void tick_shouldShiftTrack() {
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
    public void down_shouldMoveBikeToDown() {
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
    public void up_shouldMoveBikeToUp() {
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
    public void crush_shouldFallBike() {
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
    public void tick_shouldIgnoreMovingAfterBikeIsFallen() {
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
    public void inhibitorTick1_shouldChangeBikeStateToAtInhibitor_atTheBeginning() {
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
    public void inhibitorTick2_shouldChangeBikeStateToNormal_atTheBeginning() {
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
    public void inhibitor_shouldChangeBikeStateToAtInhibitor_atTheEnding() {
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
    public void inhibitor_shouldChangeBikeStateToAtInhibitor_afterBikeMoveUp() {
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
    public void inhibitorTick1_shouldChangeStateToInhibited() {
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
    public void inhibitorTick2_shouldMoveInhibitedBikeOneStepBack() {
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
    public void inhibitorTick3_shouldChangeBikeStateToNormalAndMoveBackInhibitor() {
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
    public void twoInhibitorsTick1_shouldChangeStateToInhibited() {
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
    public void twoInhibitorsTick2_shouldChangeKeepBikeStateInhibitedAndMoveItBack() {
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
    public void twoInhibitorsTick3_shouldChangeKeepBikeStateInhibited() {
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
    public void twoInhibitorsTick4_shouldChangeBikeStateToNormal() {
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
    public void accelerator_shouldChangeBikeStateToAtAccelerator_atTheBeginning() {
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
    public void accelerator_shouldChangeBikeStateToAtAccelerator_atTheEnding() {
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
    public void accelerator_shouldChangeBikeStateToAtAccelerator_afterBikeMoveUp() {
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
    public void acceleratorTick1_shouldChangeStateToAccelerated() {
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
    public void acceleratorTick2_shouldMoveAcceleratedBikeOneStepForwardAndChangeStateToNormalAndMoveAcceleratorBack() {
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
    public void acceleratorTick3_shouldMoveBackAccelerator() {
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
    public void twoAcceleratorsTick1_shouldChangeBikeStateToAccelerated() {
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
    public void twoAcceleratorsTick2_shouldMoveBikeForwardAndChangeStateToNormal() {
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
    public void twoAcceleratorsTick3_shouldChangeKeepBikeStateNormalButMoveItForward() {
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
    public void obstacleTick1_shouldObstructBike() {
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
    public void obstacleTick2_shouldMoveObstructedBikeLeft() {
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
    public void obstacleTick3_shouldMoveObstructedBikeLeft() {
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
    public void obstacleTick4_shouldMoveObstructedBikeLeft() {
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
    public void obstacleTick5_shouldRespawnObstructedBike_ifItIsLastCell() {
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
    public void obstacleAndUpCommand_shouldMoveBikeUp() {
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
    public void tick2_shouldMoveObstructedBikeAndObstacleTogether() {
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
    public void tick1_shouldMakeBikeObstructed_IfBikeInteractedWithObstacle() {
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
    public void tick2_shouldRespawnObstructedBike_ifItIsFirstColumn() {
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
    public void acceleratorAndInhibitorTick1_shouldChangeBikeStateToAccelerated() {
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
    public void acceleratorAndInhibitorTick2_shouldChangeBikeStateToInhibited() {
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
    public void acceleratorAndInhibitorTick3_shouldChangeBikeStateToNormal() {
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
    public void acceleratorAndObstacleTick1_shouldChangeBikeStateToAccelerated() {
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
    public void acceleratorAndObstacleTick2_shouldChangeBikeStateToFallen() {
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
    public void acceleratorAndLineChangerUpTick1_shouldChangeBikeStateToAccelerated() {
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
    public void acceleratorAndLineChangerUpTick2_shouldMoveBikeForwardAndUp() {
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
    public void acceleratorAndLineChangerDownTick1_shouldChangeBikeStateToAccelerated() {
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
    public void acceleratorAndLineChangerDownTick2_shouldMoveBikeForwardAndDown() {
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
    public void lineChangerUpTick1_shouldChangeBikeStateToAtLineChangerUp() {
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
    public void lineChangerUpTick2_shouldMoveBikeUpAndChangeStateBackToNormal() {
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
    public void lineChangerDownTick1_shouldChangeBikeStateToAtLineChangerDown() {
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
    public void lineChangerDownTick2_shouldMoveBikeDownAndChangeStateBackToNormal() {
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
    public void lineChangerUpAndCommandUpTick1_shouldMoveBikeUpAndKeepStateNormal() {
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
    public void lineChangerUpAndCommandUpTick2_shouldNotMoveBikeAndKeepState() {
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
    public void lineChangerUpTick2AndCommandDown_shouldNotMoveBikeAndChangeStateBackToNormal() {
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
    public void lineChangerDownAndCommandDownTick1_shouldMoveBikeDownAndKeepStateNormal() {
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
    public void lineChangerDownAndCommandDownTick2_shouldNotMoveBikeAndChangeState() {
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
    public void lineChangerDownTick2AndCommandUp_shouldNotMoveBikeAndChangeStateBackToNormal() {
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
    public void inhibitorAfterInhibitorTick1_shouldChangeStateToInhibited() {
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
    public void inhibitorAfterInhibitorTick2_shouldMoveBikeBackAndKeepStateInhibited() {
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
    public void inhibitorAfterInhibitorTick3_shouldChangeStateToNormal() {
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
    public void inhibitorAfterInhibitorTick4_shouldKeepStateNormal() {
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
    public void inhibitorAfterInhibitorTick5_shouldChangeStateToInhibited() {
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
    public void inhibitorAfterInhibitorTick6_shouldMoveInhibitedBikeBack() {
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
    public void inhibitorAfterInhibitorTick7_shouldChangeStateToNormal() {
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
    public void twoLineChangersUpTick1_shouldChangeBikeStateToAtLineChangerUp() {
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
    public void twoLineChangersUpTick2_shouldMoveBikeUpAndChangeStateToNormal() {
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
    public void lineChangerUpAndDownTick1_shouldChangeBikeStateToAtLineChangerUp() {
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
    public void lineChangerUpAndDownTick2_shouldMoveBikeUpAndChangeStateToNormal() {
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
    public void commandUpToAcceleratorTick1_shouldMoveBikeUpAndChangeStateToAccelerated() {
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
    public void acceleratorWithSpaceAndObstacleTick1_shouldChangeBikeStateToAccelerated() {
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
    public void acceleratorWithSpaceAndObstacleTick2_shouldMoveBikeForwardAndChangeStateToObstructed() {
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
    public void left_shouldDoNothing() {
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
    public void right_shouldDoNothing() {
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
    public void act_shouldDoNothing() {
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
    public void lineChangerDownAndObstacleTick1_shouldChangeBikeStateToAtLineChangerDown() {
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
    public void lineChangerDownAndObstacleTick2_shouldMoveBikeDownAndChangeStateToNormal() {
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
    public void lineChangerUpAndObstacleTick1_shouldChangeBikeStateToAtLineChangerUp() {
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
    public void lineChangerUpAndObstacleTick2_shouldMoveBikeUpAndChangeStateToNormal() {
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
    public void lineChangerDownAndAcceleratorTick1_shouldChangeBikeStateToAtLineChangerDown() {
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
    public void lineChangerDownAndAcceleratorTick2_shouldMoveBikeDownAndChangeStateToNormal() {
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
    public void lineChangerDownAndAcceleratorTick3_shouldDoNothing() {
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
    public void inhibitorAndAcceleratorTick1_shouldChangeStateToInhibited() {
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
    public void inhibitorAndAcceleratorTick2_shouldMoveBikeBack() {
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
    public void inhibitorAndAcceleratorTick3_shouldChangeBikeStateToAccelerated() {
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
    public void inhibitorAndAcceleratorTick4_shouldChangeMoveBikeForwardAndChangeStateToNormal() {
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
    public void lineChangerUpAndObstacleUpperTick1_shouldChangeBikeStateToAtLineChangerUp() {
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
    public void lineChangerUpAndObstacleUpperTick2_shouldMoveBikeUpAndChangeStateToFallenAtObstacle() {
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
    public void twoAcceleratorsAndLineChangerUpTick1_shouldChangeBikeStateToAccelerated() {
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
    public void twoAcceleratorsAndLineChangerUpTick2_shouldChangeBikeStateToAtLineChangerUp() {
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
    public void twoAcceleratorsAndLineChangerUpTick3_shouldMoveBikeUpAndRightAndChangeStateToNormal() {
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
    public void twoAcceleratorsAndLineChangerDownTick1_shouldChangeBikeStateToAccelerated() {
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
    public void twoAcceleratorsAndLineChangerDownTick2_shouldChangeBikeStateToAtLineChangerDown() {
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
    public void twoAcceleratorsAndLineChangerDownTick3_shouldMoveBikeDownAndRightAndChangeStateToNormal() {
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
    public void tick_shouldMoveBikeUp_ifBikeCrossRiseOfSpringboard() {
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
    public void tick_shouldMoveBikeUp_ifBikeCrossRiseOfSpringboard2() {
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
    public void tick_shouldMoveBikeUp_ifBikeCrossRiseOfSpringboard3() {
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
    public void tick_shouldSetStateBikeInFlight() {
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
    public void tick_shouldCrushBikeAtFence_ifBikeWasInFlight() {
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
    public void tick_shouldCrushBikeAtFence_ifBikeWasInFlight2() {
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
    public void tick_shouldCrushBike_ifBikeMoveUpOnSpringboardFence() {
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
    public void tick_shouldMoveBikeDown_ifBikeCrossDescentOfSpringboard() {
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
    public void tick_shouldMoveBikeDown_ifBikeCrossDescentOfSpringboard2() {
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
    public void tick_shouldMoveBikeDown_ifBikeCrossDescentOfSpringboard3() {
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
    public void tick_shouldMoveBikeDown_ifBikeCrossDescentOfSpringboard4() {
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
    public void tick_shouldNotMoveBikeDown_ifBikeCommandDownBeforeSpringboardRise() {
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
    public void tick_shouldNotMoveBikeDown_ifBikeCommandDownBeforeSpringboardRise2() {
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
    public void tick_shouldNotMoveBikeUp_ifBikeCommandUpAtSpringboardRise() {
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
    public void tick_shouldNotMoveBikeDownAndSetInFlightState_ifBikeCommandDownAtSpringboardRise() {
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
    public void tick_shouldNotMoveBikeUpAndCrushIt_ifBikeCommandUpAtSpringboardRise() {
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
    public void tick_shouldNotMoveBikeDown_ifBikeCommandDownBeforeSpringboardDescent() {
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
    public void tick_shouldMoveBikeDownAndCrushIt_ifBikeCommandDownAtSpringboardDescent() {
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
    public void tick_shouldMoveBikeUpAndCrushIt_ifBikeCommandUpAtSpringboardDescent() {
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
    public void tick2_shouldSpawnStraightObstaclesLineWithOneExit_ifDiceReturnsAccordingNumbers() {
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
    public void tick1_shouldSpawnEmptyLine_ifDiceReturnsAccordingForObstacleLadderUpNumbers() {
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
    public void tick2_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick3_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick4_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick5_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick6_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick7_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick8_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick9_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick10_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick11_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick12_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick13_shouldSpawnLadderUpObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick1_shouldSpawnEmptyLine_ifDiceReturnsAccordingToObstacleLadderDownNumbers() {
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
    public void tick2_shouldSpawnEmptyLineWhichIsExit_ifDiceReturnsAccordingToObstacleLadderDownNumbers() {
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
    public void tick3_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick4_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick5_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick6_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick7_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick8_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick9_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick10_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick11_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick12_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick13_shouldSpawnLadderDownObstaclesLineWithOneExitAndWidthTen_ifDiceReturnsAccordingNumbers() {
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
    public void tick6_shouldSpawnLadderUpAndDownObstaclesLineWithOneExitAndWidthFour_ifDiceReturnsAccordingNumbers() {
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
    public void tick6_shouldSpawnLadderDownAndUpObstaclesLineWithOneExitAndWidthFour_ifDiceReturnsAccordingNumbers() {
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
    public void commandUpAtLineChangerDown_shouldNotMoveBike() {
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
    public void commandDownAtLineChangerUp_shouldNotMoveBike() {
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
    public void commandDownAtLineChangerDown_shouldMoveBikeForTwoCells() {
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
    public void commandUpAtLineChangerUp_shouldMoveBikeForTwoCells() {
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
    public void tick_shouldNotMoveBikeDown_ifBikeJustSpawnedAtSpringboard() {
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
    public void bikeInFrontOfObstacleAtLine1_tick1_shouldBeCrushedAtObstacle() {
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
    public void bikeInFrontOfObstacleAtLine1_tick2_shouldBeRespawnedAtFreeSpace() {
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
