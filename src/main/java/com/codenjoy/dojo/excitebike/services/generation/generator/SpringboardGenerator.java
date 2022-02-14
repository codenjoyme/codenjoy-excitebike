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

import com.codenjoy.dojo.excitebike.model.items.Shiftable;
import com.codenjoy.dojo.excitebike.model.items.Springboard;
import com.codenjoy.dojo.games.excitebike.Element;
import com.codenjoy.dojo.services.Dice;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.games.excitebike.Element.*;

public class SpringboardGenerator implements Generator {

    public static final int SPRINGBOARD_TOP_MAX_WIDTH = 5;
    static final int CLEAR_LINES_AROUND_SPRINGBOARD = 1;
    private final Dice dice;
    private final int x0;
    private final int ySize;
    private int width;
    private Map<Element, List<Shiftable>> elements;

    public SpringboardGenerator(Dice dice, int xSize, int ySize) {
        this.x0 = xSize - 1 + CLEAR_LINES_AROUND_SPRINGBOARD;
        this.ySize = ySize;
        this.dice = dice;
    }

    @Override
    public Map<Element, List<Shiftable>> generate() {
        elements = new EnumMap<>(Element.class);
        width = dice.next(SPRINGBOARD_TOP_MAX_WIDTH) + 2;
        generateRiseLine(ySize);
        for (int i = x0 + 1; i < x0 + width - 1; i++) {
            generateSpringBoardStep(SPRINGBOARD_TOP, SPRINGBOARD_TOP, SPRINGBOARD_LEFT, i, ySize);
        }
        generateDescentLine(x0 + width, ySize);
        return elements;
    }

    private void generateRiseLine(int linesNumber) {
        generateSpringBoardStep(SPRINGBOARD_LEFT_UP, SPRINGBOARD_LEFT, SPRINGBOARD_LEFT_DOWN, x0, linesNumber);
    }

    private void generateDescentLine(int x, int linesNumber) {
        generateSpringBoardStep(SPRINGBOARD_RIGHT_UP, SPRINGBOARD_RIGHT, SPRINGBOARD_RIGHT_DOWN, x - 1, linesNumber);
    }

    private void generateSpringBoardStep(Element up, Element middle, Element down, int x, int lines) {
        addNewElement(down, x, 1);
        int y = 2;
        for (; y < lines - 1; y++) {
            addNewElement(middle, x, y);
        }
        addNewElement(up, x, y);
    }

    private void addNewElement(Element type, int x, int y) {
        // TODO do not use map.containsKey just check that map.get() != null
        if (!elements.containsKey(type)) {
            elements.put(type, new LinkedList<>());
        }
        elements.get(type).add(new Springboard(x, y, type));
    }

    @Override
    public int generationLockSize() {
        return width + CLEAR_LINES_AROUND_SPRINGBOARD * 2;
    }
}
