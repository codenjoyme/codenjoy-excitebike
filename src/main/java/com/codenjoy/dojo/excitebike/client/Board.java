package com.codenjoy.dojo.excitebike.client;

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


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.excitebike.model.elements.BikeType;
import com.codenjoy.dojo.excitebike.model.elements.GameElementType;
import com.codenjoy.dojo.excitebike.model.elements.SpringboardElementType;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.printer.CharElements;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс, обрабатывающий строковое представление доски.
 * Содержит ряд унаследованных методов {@see AbstractBoard},
 * но ты можешь добавить сюда любые свои методы на их основе.
 */
public class Board extends AbstractBoard<CharElements> {

    private static final String OTHER_BIKE_PREFIX = "OTHER";
    private static final String FALLEN_BIKE_SUFFIX = "FALLEN";

    @Override
    public CharElements valueOf(char ch) {
        return Stream.of(
                Arrays.stream(GameElementType.values()),
                Arrays.stream(SpringboardElementType.values()),
                Arrays.stream(BikeType.values())
        ).flatMap(Function.identity())
                .filter(e -> ((CharElements)e).ch() == ch)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such element for " + ch));
    }

    public Point getMe() {
        return get(Arrays.stream(BikeType.values())
                .filter(v -> !v.name().contains(OTHER_BIKE_PREFIX))
                .collect(Collectors.toList())
                .toArray(new BikeType[]{}))
                .stream()
                .findFirst()
                .orElse(null);
    }

    public boolean isGameOver() {
        Point me = getMe();
        return me == null || Arrays.stream(BikeType.values())
                .filter(v -> !v.name().contains(OTHER_BIKE_PREFIX) && v.name().contains(FALLEN_BIKE_SUFFIX))
                .anyMatch(v -> isAt(me, v));
    }

    public boolean checkNearMe(List<Direction> directions, CharElements... elements) {
        Point point = getMe();
        if (point == null) {
            return false;
        }
        for (Direction direction : directions) {
            point = direction.change(point);
        }
        return isAt(point.getX(), point.getY(), elements);
    }

    public boolean checkNearMe(Direction direction, CharElements... elements) {
        Point me = getMe();
        if (me == null) {
            return false;
        }
        Point atDirection = direction.change(me);
        return isAt(atDirection.getX(), atDirection.getY(), elements);
    }

    public boolean checkAtMe(CharElements... elements) {
        Point me = getMe();
        return me != null && isAt(me, elements);
    }

    public boolean isOutOfFieldRelativeToMe(Direction direction) {
        Point me = getMe();
        if (me == null) {
            return false;
        }
        Point atDirection = direction.change(me);
        return isOutOfField(atDirection.getX(), atDirection.getY());
    }

    @Override
    public String toString() {
        String superToString = super.toString();
        int indexOfFirstFence = superToString.indexOf(GameElementType.FENCE.ch());
        int nextLineStatementLength = 2;
        return superToString.substring(indexOfFirstFence >= nextLineStatementLength
                ? indexOfFirstFence - nextLineStatementLength
                : indexOfFirstFence);
    }
}
