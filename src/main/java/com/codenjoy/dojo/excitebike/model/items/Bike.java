package com.codenjoy.dojo.excitebike.model.items;

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


import com.codenjoy.dojo.excitebike.model.Field;
import com.codenjoy.dojo.excitebike.model.Player;
import com.codenjoy.dojo.games.excitebike.element.BikeElement;
import com.codenjoy.dojo.excitebike.services.Event;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.printer.state.State;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;

import java.util.Objects;
import java.util.Optional;

import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_ACCELERATOR;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_INHIBITOR;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_KILLED_BIKE;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_LINE_CHANGER_DOWN;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_LINE_CHANGER_UP;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_SPRINGBOARD_LEFT;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_SPRINGBOARD_LEFT_DOWN;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_SPRINGBOARD_RIGHT;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_AT_SPRINGBOARD_RIGHT_DOWN;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_FALLEN;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_FALLEN_AT_ACCELERATOR;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_FALLEN_AT_FENCE;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_FALLEN_AT_INHIBITOR;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_FALLEN_AT_LINE_CHANGER_DOWN;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_FALLEN_AT_LINE_CHANGER_UP;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_FALLEN_AT_OBSTACLE;
import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_IN_FLIGHT_FROM_SPRINGBOARD;
import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.Direction.LEFT;
import static com.codenjoy.dojo.services.Direction.RIGHT;
import static com.codenjoy.dojo.services.Direction.UP;

public class Bike extends PlayerHero<Field> implements State<BikeElement, Player>, Shiftable {

    public static final String OTHER_BIKE_PREFIX = "OTHER";
    public static final String FALLEN_BIKE_SUFFIX = "FALLEN";
    public static final String BIKE_AT_PREFIX = "AT_";
    public static final String AT_ACCELERATOR_SUFFIX = "_AT_ACCELERATOR";
    public static final String AT_INHIBITOR_SUFFIX = "_AT_INHIBITOR";
    public static final String AT_LINE_CHANGER_UP_SUFFIX = "_AT_LINE_CHANGER_UP";
    public static final String AT_LINE_CHANGER_DOWN_SUFFIX = "_AT_LINE_CHANGER_DOWN";

    private Direction command;
    private Movement movement = new Movement();
    private BikeElement type = BIKE;
    private boolean ticked;
    private boolean accelerated;
    private boolean inhibited;
    private boolean interacted;
    private boolean atSpringboard;
    private boolean adjusted;
    private boolean movementLock;

    public Bike(Point xy) {
        super(xy);
    }

    public Bike(int x, int y) {
        super(x, y);
    }

    @Override
    public void init(Field gameField) {
        this.field = gameField;
        adjusted = false;
        adjustStateToElement();
        adjusted = false;
    }

    @Override
    public void down() {
        if (!isAlive()) return;
        command = DOWN;
    }

    @Override
    public void up() {
        if (!isAlive()) return;
        command = UP;
    }

    @Override
    public void left() {
    }

    @Override
    public void right() {
    }

    public void crush() {
        type = type == BIKE_AT_ACCELERATOR ? BIKE_FALLEN_AT_ACCELERATOR :
                type == BIKE_AT_INHIBITOR ? BIKE_FALLEN_AT_INHIBITOR :
                        type == BIKE_AT_LINE_CHANGER_DOWN ? BIKE_FALLEN_AT_LINE_CHANGER_DOWN :
                                type == BIKE_AT_LINE_CHANGER_UP ? BIKE_FALLEN_AT_LINE_CHANGER_UP :
                                        BIKE_FALLEN;
    }

    public void crushLikeEnemy(BikeElement crushedEnemyBikeType) {
        type = BikeElement.valueOf(crushedEnemyBikeType.name().replace(OTHER_BIKE_PREFIX + "_", ""));
    }

    @Override
    public void act(int... p) {
        //nothing to do
    }

    @Override
    public void tick() {
        if (!ticked && isAlive()) {
            adjusted = false;
            actAccordingToState();
            if (movementLock) {
                command = null;
                accelerated = false;
            }
            executeCommand();
            tryToMove();
            adjustStateToElement();
            if (!isAlive()) {
                field.getPlayerOfBike(this).event(Event.LOSE);
            }
        }
    }

    private void executeCommand() {
        interacted = false;
        if (command != null) {
            x = command.changeX(x);
            y = command.changeY(y);
            interactWithOtherBike();
            command = null;
            adjustStateToElement();
        }
    }

    private void actAccordingToState() {
        if (type == BIKE_AT_INHIBITOR) {
            if (!inhibited) {
                movement.setLeft();
                inhibited = true;
            }
            type = atNothingType();
        } else {
            inhibited = false;
        }

        if (type == BIKE_AT_LINE_CHANGER_UP) {
            movement.setUp();
            type = atNothingType();
        }

        if (type == BIKE_AT_LINE_CHANGER_DOWN) {
            movement.setDown();
            type = atNothingType();
        }

        if (type == BIKE_AT_ACCELERATOR || accelerated) {
            movement.setRight();
            type = atNothingType();
            accelerated = false;
            adjustStateToElement();
        }

        if (type == BIKE_AT_SPRINGBOARD_LEFT
                || type == BIKE_AT_SPRINGBOARD_LEFT_DOWN
                || type == BIKE_AT_SPRINGBOARD_RIGHT
                || type == BIKE_AT_SPRINGBOARD_RIGHT_DOWN) {
            movementLock = field.isSpringboardRightElement(x, y) || field.isSpringboardRightDownElement(x, y);
            type = atNothingType();
            return;
        }

        if (type == BIKE_IN_FLIGHT_FROM_SPRINGBOARD) {
            movementLock = true;
            movement.setDown();
        }

    }

    private BikeElement atNothingType() {
        return type.name().contains(BIKE_AT_PREFIX)
                ? BikeElement.valueOf(type.name().substring(0, type.name().indexOf(BIKE_AT_PREFIX) - 1))
                : type;
    }

    private void tryToMove() {
        int xBefore = x;
        int yBefore = y;
        if (!isAlive()) {
            return;
        }
        if (movement.isUp()) {
            y = UP.changeY(y);
        }
        if (movement.isDown()) {
            y = DOWN.changeY(y);
        }
        if (movement.isLeft()) {
            x = LEFT.changeX(x);
            if (isAlive() && x < 0) {
                x = 0;
            }
        }
        if (movement.isRight()) {
            x = RIGHT.changeX(x);
            if (x >= field.xSize()) {
                x = field.xSize() - 1;
            }
        }
        interactWithOtherBike();
        movement.clear();
        if (xBefore != x || yBefore != y) {
            adjusted = false;
        }
    }

    private void interactWithOtherBike() {
        if (interacted) {
            return;
        }
        field.getEnemyBike(x, y, field.getPlayerOfBike(this)).ifPresent(enemy -> {
            if (enemy != this) {
                if (!enemy.isAlive()) {
                    crushLikeEnemy(enemy.getEnemyBikeType());
                    return;
                }
                if (movement.isRight()
                        && !enemy.movement.isRight()
                        && (enemyDoesNotMoveUpOrDown(enemy) || enemyCouldAvoidThisBikeButCantBecauseOfInteractionWithOtherBike(enemy))) {
                    enemy.type = BIKE_AT_KILLED_BIKE;
                    crush();
                    return;
                }
                if (twoBikesAreMovingUpOrDownToEachOther(this, enemy)) {
                    enemy.clearY();
                    if (movement.isUp() || command == UP) {
                        move(x, DOWN.changeY(y));
                    } else if (movement.isDown() || command == DOWN) {
                        move(x, UP.changeY(y));
                    }
                    clearY();
                    return;
                }
                if (enemyDoesNotMoveUpOrDown(enemy) || enemyCouldAvoidThisBikeButCantBecauseOfInteractionWithOtherBike(enemy)) {
                    enemy.crush();
                    type = BIKE_AT_KILLED_BIKE;
                    field.getPlayerOfBike(this).event(Event.WIN);
                    move(enemy);
                    enemy.ticked = true;
                    field.getPlayerOfBike(enemy).event(Event.LOSE);
                    movement.clear();
                    command = null;
                }
            }
        });
        interacted = true;
    }

    private boolean twoBikesAreMovingUpOrDownToEachOther(Bike one, Bike another) {
        return (one.movement.isDown() || one.command == DOWN) && (another.movement.isUp() || another.command == UP)
                || (one.movement.isUp() || one.command == UP) && (another.movement.isDown() || another.command == DOWN);
    }

    private boolean enemyDoesNotMoveUpOrDown(Bike enemy) {
        return !enemy.movement.isUp() && !enemy.movement.isDown() && enemy.command != UP && enemy.command != DOWN;
    }

    private boolean enemyCouldAvoidThisBikeButCantBecauseOfInteractionWithOtherBike(Bike enemy) {
        if (movement.isRight() && enemy.movement.isRight()
                || (movement.isUp() || command == UP) && (enemy.movement.isUp() || enemy.command == UP)
                || (movement.isDown() || command == DOWN) && (enemy.movement.isDown() || enemy.command == DOWN)) {
            int enemyDestinationX = enemy.x;
            int enemyDestinationY = enemy.y;
            if (enemy.command != null) {
                enemyDestinationY = command.changeY(enemyDestinationY);
            }
            if (enemy.movement.isLeft() && enemy.x > 0) {
                enemyDestinationX = LEFT.changeX(enemyDestinationX);
            }
            if (enemy.movement.isRight() && enemy.x >= field.xSize()) {
                enemyDestinationX = RIGHT.changeX(enemyDestinationX);
            }
            if (enemy.movement.isUp()) {
                enemyDestinationY = UP.changeY(enemyDestinationY);
            }
            if (enemy.movement.isDown()) {
                enemyDestinationY = DOWN.changeY(enemyDestinationY);
            }
            Optional<Bike> enemyOfTheEnemy = field.getEnemyBike(enemyDestinationX, enemyDestinationY, field.getPlayerOfBike(enemy));
            return enemyOfTheEnemy.isPresent() && twoBikesAreMovingUpOrDownToEachOther(enemy, enemyOfTheEnemy.get());
        }
        return false;
    }

    private void clearY() {
        if (movement.isUp()) {
            movement.setDown();
        } else if (movement.isDown()) {
            movement.setUp();
        }
        command = null;
    }

    private void adjustStateToElement() {
        if (!isAlive() || adjusted) {
            return;
        }
        adjusted = true;

        if (isNextStepSpringboardRiseOrDecent()) {
            movementLock = true;
        }

        if (field.isSpringboardLeftOrDownElement(x, y)) {
            if (y == 1 && !movement.isUp()) {
                type = BIKE_IN_FLIGHT_FROM_SPRINGBOARD;
                atSpringboard = false;
            } else {
                type = BIKE_AT_SPRINGBOARD_LEFT;
                atSpringboard = true;
            }
            return;
        }

        if (field.isSpringboardRightElement(x, y)) {
            type = BIKE_AT_SPRINGBOARD_RIGHT;
            atSpringboard = false;
            return;
        }

        if (field.isSpringboardLeftDownElement(x, y)) {
            type = BIKE_AT_SPRINGBOARD_LEFT_DOWN;
            atSpringboard = true;
            return;
        }

        if (field.isSpringboardRightDownElement(x, y)) {
            type = BIKE_AT_SPRINGBOARD_RIGHT_DOWN;
            atSpringboard = false;
            return;
        }

        if (field.isSpringboardTopElement(x, y)) {
            atSpringboard = true;
        }

        if (field.isAccelerator(x, y)) {
            changeStateToAt(AT_ACCELERATOR_SUFFIX);
            accelerated = true;
            return;
        }

        if (field.isInhibitor(x, y)) {
            if (type.name().contains(AT_ACCELERATOR_SUFFIX)) {
                type = BikeElement.valueOf(type.name().replace(AT_ACCELERATOR_SUFFIX, ""));
            } else {
                changeStateToAt(AT_INHIBITOR_SUFFIX);
            }
            if (movement.isRight()) {
                movement.setLeft();
                inhibited = true;
            }
            return;
        }

        if (field.isObstacle(x, y)) {
            if (movement.isRight()) {
                movement.setLeft();
            }
            type = BIKE_FALLEN_AT_OBSTACLE;
            return;
        }

        if (field.isUpLineChanger(x, y)) {
            if (movement.isRight()) {
                movement.setUp();
            } else {
                changeStateToAt(AT_LINE_CHANGER_UP_SUFFIX);
            }
            return;
        }

        if (field.isDownLineChanger(x, y)) {
            if (movement.isRight()) {
                movement.setDown();
            } else {
                changeStateToAt(AT_LINE_CHANGER_DOWN_SUFFIX);
            }
            return;
        }

        if (field.isFence(x, y) && !atSpringboard) {
            type = BIKE_FALLEN_AT_FENCE;
            return;
        }

        if (atSpringboard && y >= field.ySize()) {
            crush();
            return;
        }

        if (!field.getEnemyBike(x, y, field.getPlayerOfBike(this)).isPresent()) {
            type = atNothingType();
        }
    }

    private void changeStateToAt(String atSuffix) {
        if (type.name().contains(BIKE_AT_PREFIX)) {
            String substringBikeAtSomething = type.name().substring(type.name().indexOf(BIKE_AT_PREFIX) - 1);
            type = BikeElement.valueOf(type.name().replace(substringBikeAtSomething, atSuffix));
        } else {
            type = BikeElement.valueOf(type.name() + atSuffix);
        }
    }

    @Override
    public BikeElement state(Player player, Object... alsoAtPoint) {
        Bike bike = player.getHero();
        return this == bike ? bike.type : this.getEnemyBikeType();
    }

    private BikeElement getEnemyBikeType() {
        return BikeElement.valueOf(OTHER_BIKE_PREFIX + "_" + type.name());
    }

    @Override
    public boolean isAlive() {
        return type != null && !type.name().contains(FALLEN_BIKE_SUFFIX);
    }

    public void resetTicked() {
        ticked = false;
    }

    public void changeYDependsOnSpringboard() {
        if (type == BIKE_AT_SPRINGBOARD_LEFT
                || type == BIKE_AT_SPRINGBOARD_LEFT_DOWN) {
            y++;
        }
        if (field.isSpringboardRightElement(x, y - 1) || field.isSpringboardRightDownElement(x, y - 1)) {
            y--;
        }
    }

    private boolean isNextStepSpringboardRiseOrDecent() {
        return field.isSpringboardRightElement(x + 1, y - 1)
                || field.isSpringboardRightDownElement(x + 1, y - 1)
                || field.isSpringboardLeftOrDownElement(x + 1, y)
                || field.isSpringboardLeftDownElement(x + 1, y);
    }

    @Override
    public String toString() {
        return "Bike{" +
                "command=" + command +
                ", movement=" + movement +
                ", type=" + type +
                ", ticked=" + ticked +
                ", accelerated=" + accelerated +
                ", inhibited=" + inhibited +
                ", interacted=" + interacted +
                ", atSpringboard=" + atSpringboard +
                ", adjusted=" + adjusted +
                ", movementLock=" + movementLock +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    class Movement {

        private boolean up;
        private boolean down;
        private boolean left;
        private boolean right;

        public Movement() {
        }

        public boolean isUp() {
            return up;
        }

        public void setUp() {
            if (down) {
                down = false;
            } else {
                up = true;
            }
        }

        public boolean isDown() {
            return down;
        }

        public void setDown() {
            if (up) {
                up = false;
            } else {
                down = true;
            }
        }

        public boolean isLeft() {
            return left;
        }

        public void setLeft() {
            if (right) {
                right = false;
            } else {
                left = true;
            }
        }

        public boolean isRight() {
            return right;
        }

        public void setRight() {
            if (left) {
                left = false;
            } else {
                right = true;
            }
        }

        public void clear() {
            up = false;
            down = false;
            left = false;
            right = false;
        }

        public boolean isClean() {
            return !up && !down && !left && !right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Movement movement = (Movement) o;
            return up == movement.up &&
                    down == movement.down &&
                    left == movement.left &&
                    right == movement.right;
        }

        @Override
        public int hashCode() {
            return Objects.hash(up, down, left, right);
        }

        @Override
        public String toString() {
            return "Movement{" +
                    "up=" + up +
                    ", down=" + down +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }
}
