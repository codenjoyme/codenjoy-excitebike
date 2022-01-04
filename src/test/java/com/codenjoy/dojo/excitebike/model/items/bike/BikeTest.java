package com.codenjoy.dojo.excitebike.model.items.bike;

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
import com.codenjoy.dojo.excitebike.model.Field;
import com.codenjoy.dojo.excitebike.model.Player;
import com.codenjoy.dojo.excitebike.model.items.Bike;
import com.codenjoy.dojo.excitebike.services.Event;
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.games.excitebike.element.BikeElement;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.codenjoy.dojo.excitebike.TestUtils.getPlayer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class BikeTest {

    private Bike bike;
    private Field gameField;
    private GameSettings settings;

    @Before
    public void init() {
        bike = new Bike(5, 5);
        gameField = mock(Field.class);
        when(gameField.xSize()).thenReturn(10);
        bike.init(gameField);
        settings = new TestGameSettings();
    }

    @Test
    public void tick_shouldNotShiftBike_ifBikeIsNotAlive() {
        //given
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        bike.crush();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
    }

    @Test
    public void tickWithUpCommand_shouldMoveBikeToUp_ifUpperPositionIsFree() {
        //given
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());
        bike.up();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(6));
    }

    @Test
    public void tickWithUpCommand_shouldMoveAndCrushBike_ifUpperPositionIsFence() {
        //given
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(true);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());
        bike.up();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(6));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_FALLEN_AT_FENCE));
    }

    @Test
    public void tickWithUpCommand_shouldMoveBikeAndChangeItsStateToKilledEnemy_ifUpperPositionIsOtherBike() {
        //given
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        Bike enemy = new Bike(5, 6);
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getEnemyBike(5, 6, player)).thenReturn(Optional.of(enemy));
        when(gameField.getPlayerOfBike(enemy)).thenReturn(player);
        enemy.init(gameField);
        bike.up();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(6));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_KILLED_BIKE));
    }

    @Test
    public void tickWithDownCommand_shouldMoveBikeToDown_ifLowerPositionIsFree() {
        //given
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());
        bike.down();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(4));
    }

    @Test
    public void tickWithDownCommand_shouldMoveBikeAndCrushIt_ifLowerPositionIsFence() {
        //given
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        bike.setY(1);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(true);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());
        bike.down();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(0));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_FALLEN_AT_FENCE));
    }

    @Test
    public void tickWithDownCommand_shouldMoveBikeAndChangeItsStateToKilledEnemy_ifLowerPositionIsOtherBike() {
        //given
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        Bike enemy = new Bike(5, 4);
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getEnemyBike(5, 4, player)).thenReturn(Optional.of(enemy));
        when(gameField.getPlayerOfBike(enemy)).thenReturn(player);
        enemy.init(gameField);
        bike.down();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(4));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_KILLED_BIKE));
    }

    @Test
    public void tick1_shouldNotChangeBikePositionAndChangeStateToAccelerated_ifBikeTakeAccelerator() {
        //given
        when(gameField.isAccelerator(anyInt(), anyInt())).thenReturn(true);
        when(gameField.xSize()).thenReturn(8);

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_ACCELERATOR));
    }

    @Test
    public void tick2_shouldSetBikeXCoordinateToMaxPossible_ifBikePositionAfterAccelerationIsOutOfFieldBound() {
        //given
        when(gameField.isAccelerator(5, 5)).thenReturn(true);
        when(gameField.xSize()).thenReturn(6);
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_ACCELERATOR));
    }

    @Test
    public void tick1_shouldNotChangeBikePosition_ifBikeTakeInhibitor() {
        //given
        when(gameField.isInhibitor(anyInt(), anyInt())).thenReturn(true);

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_INHIBITOR));
    }

    @Test
    public void tick2_shouldMoveBikeBackAndKeepInhibitedState_ifBikeTakeInhibitor() {
        //given
        when(gameField.isInhibitor(anyInt(), anyInt())).thenReturn(true);
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(4));
        assertThat(bike.getY(), is(5));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_INHIBITOR));
    }

    @Test
    public void tick3_shouldNotMoveBikeBackAndChangeStateToNormal_ifBikeTakeInhibitor() {
        //given
        when(gameField.isInhibitor(5, 5)).thenReturn(true);
        bike.tick();
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(4));
        assertThat(bike.getY(), is(5));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE));
    }

    @Test
    public void tick2_shouldSetBikeXCoordinateToMinPossible_ifBikePositionAfterInhibitionIsOutOfFieldBound() {
        //given
        bike.setX(1);
        when(gameField.isInhibitor(anyInt(), anyInt())).thenReturn(true);
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(0));
        assertThat(bike.getY(), is(5));
    }

    @Test
    public void tick_shouldFallBike_ifBikeEncounterWithObstacle() {
        //given
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.isObstacle(anyInt(), anyInt())).thenReturn(true);

        //when
        bike.tick();

        //then
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_FALLEN_AT_OBSTACLE));
    }

    @Test
    public void tick1_shouldNotChangeBikePosition_ifBikeTakeUpLineChangerAndUpperPositionIsFree() {
        //given
        when(gameField.isUpLineChanger(anyInt(), anyInt())).thenReturn(true);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
    }

    @Test
    public void tick2_shouldMoveBikeUpper_ifBikeTakeUpLineChangerAndUpperPositionIsFree() {
        //given
        when(gameField.isUpLineChanger(anyInt(), anyInt())).thenReturn(true);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(6));
    }

    @Test
    public void tick1_shouldNotMoveAndCrushBike_ifBikeTakeUpLineChangerAndUpperPositionIsFence() {
        //given
        when(gameField.isUpLineChanger(anyInt(), anyInt())).thenReturn(true);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(true);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_LINE_CHANGER_UP));
    }

    @Test
    public void tick2_shouldMoveAndCrushBike_ifBikeTakeUpLineChangerAndUpperPositionIsFence() {
        //given
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.isUpLineChanger(5, 5)).thenReturn(true);
        when(gameField.isFence(5, 6)).thenReturn(true);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(6));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_FALLEN_AT_FENCE));
    }

    @Test
    public void tick1_shouldNotMoveBike_ifBikeTakeUpLineChangerAndUpperPositionIsOtherBike() {
        //given
        when(gameField.isUpLineChanger(anyInt(), anyInt())).thenReturn(true);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        Bike enemy = new Bike(5, 6);
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getEnemyBike(5, 6, player)).thenReturn(Optional.of(enemy));
        enemy.init(gameField);

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
    }

    @Test
    public void tick2_shouldMoveBike_ifBikeTakeUpLineChangerAndUpperPositionIsOtherBike() {
        //given
        when(gameField.isUpLineChanger(anyInt(), anyInt())).thenReturn(true);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        Bike enemy = new Bike(5, 6);
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getEnemyBike(5, 6, player)).thenReturn(Optional.of(enemy));
        when(gameField.getPlayerOfBike(enemy)).thenReturn(player);
        enemy.init(gameField);
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(6));
    }

    @Test
    public void tick1_shouldNotMoveBike_ifBikeTakeDownLineChangerAndLowerPositionIsFree() {
        //given
        when(gameField.isDownLineChanger(anyInt(), anyInt())).thenReturn(true);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
    }

    @Test
    public void tick2_shouldMoveBikeDown_ifBikeTakeDownLineChangerAndLowerPositionIsFree() {
        //given
        when(gameField.isDownLineChanger(anyInt(), anyInt())).thenReturn(true);
        when(gameField.isFence(anyInt(), anyInt())).thenReturn(false);
        when(gameField.getEnemyBike(anyInt(), anyInt(), any(Player.class))).thenReturn(Optional.empty());
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(4));
    }

    @Test
    public void tick2_shouldMoveAndCrushBike_ifBikeTakeDownLineChangerAndLowerPositionIsFence() {
        //given
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.isDownLineChanger(5, 5)).thenReturn(true);
        when(gameField.isFence(5, 4)).thenReturn(true);
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(4));
        assertThat(bike.isAlive(), is(false));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_FALLEN_AT_FENCE));
    }

    @Test
    public void tick1_shouldNotMoveBike_ifBikeTakeDownLineChangerAndLowerPositionIsOtherBike() {
        //given
        when(gameField.isDownLineChanger(5, 5)).thenReturn(true);
        Bike enemy = new Bike(5, 6);
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getEnemyBike(5, 6, player)).thenReturn(Optional.of(enemy));
        enemy.init(gameField);

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_LINE_CHANGER_DOWN));
    }

    @Test
    public void tick2_shouldMoveBikeDown_ifBikeTakeDownLineChangerAndLowerPositionIsOtherBike() {
        //given
        when(gameField.isDownLineChanger(5, 5)).thenReturn(true);
        Bike enemy = new Bike(5, 4);
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getEnemyBike(5, 4, player)).thenReturn(Optional.of(enemy));
        when(gameField.getPlayerOfBike(enemy)).thenReturn(player);
        enemy.init(gameField);
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(4));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_KILLED_BIKE));
    }

    @Test
    public void tick_shouldCrushBike_ifBikeCollideOtherCrushedBike() {
        //given
        Bike enemyBike = new Bike(5, 5);
        enemyBike.crush();
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getEnemyBike(5, 5, player)).thenReturn(Optional.of(enemyBike));

        //when
        bike.tick();

        //then
        assertThat(bike.isAlive(), is(false));
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(5));
        assertThat(enemyBike.getX(), is(5));
        assertThat(enemyBike.getY(), is(5));
    }

    @Test
    public void downCommandToEnemyBikeTick_shouldCrushOtherBike_ifOtherBikeDidNotChangeLine() {
        //given
        Bike enemy = new Bike(5, 4);
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getPlayerOfBike(enemy)).thenReturn(player);
        when(gameField.getEnemyBike(5, 4, player)).thenReturn(Optional.of(enemy));
        enemy.init(gameField);

        //when
        bike.down();
        bike.tick();
        enemy.tick();

        //then
        assertThat(bike.isAlive(), is(true));
        assertThat(enemy.isAlive(), is(false));
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(4));
        assertThat(enemy.getX(), is(5));
        assertThat(enemy.getY(), is(4));
        assertThat(bike.state(getPlayer(bike, settings)), is(BikeElement.BIKE_AT_KILLED_BIKE));
    }

    @Test
    public void tick2_shouldCrushBikeAndChangeEnemyBikeStateToAtKilledBike_ifBikeAfterAcceleratorCollidesEnemyBike() {
        //given
        when(gameField.isAccelerator(5, 5)).thenReturn(true);
        when(gameField.xSize()).thenReturn(10);
        Bike enemy = new Bike(6, 5);
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.getEnemyBike(6, 5, player)).thenReturn(Optional.of(enemy));
        enemy.init(gameField);
        bike.tick();
        //enemy.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(6));
        assertThat(bike.getY(), is(5));
        assertThat(bike.isAlive(), is(false));
        assertThat(enemy.state(getPlayer(bike, settings)), is(BikeElement.OTHER_BIKE_AT_KILLED_BIKE));
        assertThat(enemy.state(getPlayer(enemy, settings)), is(BikeElement.BIKE_AT_KILLED_BIKE));
    }

    @Test
    public void tick_shouldMoveBikeDownToFlight_ifBikeGoToSpringboardDarkElementTypeUnderRoad() {
        //given
        bike.setY(2);
        bike.down();
        when(gameField.isSpringboardLeftOrDownElement(anyInt(), anyInt())).thenReturn(true);
        bike.tick();

        //when
        bike.tick();

        //then
        assertThat(bike.getX(), is(5));
        assertThat(bike.getY(), is(0));
    }

    @Test
    public void tick_shouldFireLoseEventAfterCrush() {
        //given
        Player player = mock(Player.class);
        when(gameField.getPlayerOfBike(bike)).thenReturn(player);
        when(gameField.isObstacle(bike.getX(), bike.getY())).thenReturn(true);

        //when
        bike.tick();

        //then
        verify(player).event(Event.LOSE);
    }

}
