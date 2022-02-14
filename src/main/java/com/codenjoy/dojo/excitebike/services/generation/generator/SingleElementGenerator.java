package com.codenjoy.dojo.excitebike.services.generation.generator;

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

import com.codenjoy.dojo.excitebike.model.items.*;
import com.codenjoy.dojo.games.excitebike.Element;
import com.codenjoy.dojo.games.excitebike.ElementUtils;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.printer.CharElement;
import com.google.common.collect.Lists;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.games.excitebike.Element.*;

public class SingleElementGenerator implements Generator {

    private final Dice dice;
    private final int xSize;
    private final int ySize;

    public SingleElementGenerator(Dice dice, int xSize, int ySize) {
        this.dice = dice;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    public Map<? extends CharElement, List<Shiftable>> generate() {
        int rndNonFenceElementOrdinal = dice.next(ElementUtils.stuff.length - 2) + 2;
        int rndNonFenceLaneNumber = dice.next(ySize - 2) + 1;
        CharElement randomType = ElementUtils.stuff[rndNonFenceElementOrdinal];
        int firstPossibleX = xSize - 1;
        return getNewElement(randomType, firstPossibleX, rndNonFenceLaneNumber);
    }

    private Map<Element, List<Shiftable>> getNewElement(CharElement randomType, int x, int y) {
        Map<Element, List<Shiftable>> map = new EnumMap<>(Element.class);
        if (ACCELERATOR.equals(randomType)) {
            map.put(ACCELERATOR, Lists.newArrayList(new Accelerator(x, y)));
        } else if (INHIBITOR.equals(randomType)) {
            map.put(INHIBITOR, Lists.newArrayList(new Inhibitor(x, y)));
        } else if (OBSTACLE.equals(randomType)) {
            map.put(OBSTACLE, Lists.newArrayList(new Obstacle(x, y)));
        } else if (LINE_CHANGER_UP.equals(randomType)) {
            map.put(LINE_CHANGER_UP, Lists.newArrayList(new LineChanger(x, y, true)));
        } else if (LINE_CHANGER_DOWN.equals(randomType)) {
            map.put(LINE_CHANGER_DOWN, Lists.newArrayList(new LineChanger(x, y, false)));
        }
        return map;
    }

    @Override
    public int generationLockSize() {
        return 0;
    }
}
