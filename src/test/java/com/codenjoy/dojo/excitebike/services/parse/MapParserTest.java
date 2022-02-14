package com.codenjoy.dojo.excitebike.services.parse;

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

import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.printer.CharElement;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

import static com.codenjoy.dojo.games.excitebike.Element.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@RunWith(Parameterized.class)
public class MapParserTest {

    private CharElement element;

    public MapParserTest(CharElement element) {
        this.element = element;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection data() {
        return Lists.newArrayList(
                FENCE,
                ACCELERATOR,
                INHIBITOR,
                OBSTACLE,
                LINE_CHANGER_UP,
                LINE_CHANGER_DOWN,
                SPRINGBOARD_LEFT,
                SPRINGBOARD_RIGHT,
                SPRINGBOARD_LEFT_DOWN,
                SPRINGBOARD_RIGHT_DOWN,
                SPRINGBOARD_LEFT_UP,
                SPRINGBOARD_LEFT_DOWN,
                SPRINGBOARD_TOP
        );
    }

    @Test
    public void getPointImplMethods_shouldReturnAllElementsOfCertainTypeWithCorrectCoordinates_ifGivenMapIsSquareWithDifferentObjects() {
        //given
        String map = "     " +
                "   " + element.ch() + " " +
                "  " + element.ch() + element.ch() + " " +
                "     " +
                element.ch() + "    ";
        int xSize = 5;
        MapParser mapParser = new MapParser(map, xSize);

        //when
        List<PointImpl> result = callTestMethod(mapParser);

        //then
        assertThat(result, hasSize(4));

        assertThat(result.get(0).getX(), is(3));
        assertThat(result.get(0).getY(), is(3));

        assertThat(result.get(1).getX(), is(2));
        assertThat(result.get(1).getY(), is(2));

        assertThat(result.get(2).getX(), is(3));
        assertThat(result.get(2).getY(), is(2));

        assertThat(result.get(3).getX(), is(0));
        assertThat(result.get(3).getY(), is(0));
    }

    @Test
    public void getPointImplMethods_shouldReturnAllElementsOfCertainTypeWithCorrectCoordinates_ifGivenMapIsSquareWithElementsOfCertainTypeOnly() {
        //given
        String map = "" + element.ch() + element.ch() + element.ch() +
                element.ch() + element.ch() + element.ch() +
                element.ch() + element.ch() + element.ch();
        int xSize = 3;
        MapParser mapParser = new MapParser(map, xSize);

        //when
        List<PointImpl> result = callTestMethod(mapParser);

        //then
        assertThat(result, hasSize(9));

        assertThat(result.get(0).getX(), is(0));
        assertThat(result.get(0).getY(), is(2));

        assertThat(result.get(1).getX(), is(1));
        assertThat(result.get(1).getY(), is(2));

        assertThat(result.get(2).getX(), is(2));
        assertThat(result.get(2).getY(), is(2));

        assertThat(result.get(3).getX(), is(0));
        assertThat(result.get(3).getY(), is(1));

        assertThat(result.get(4).getX(), is(1));
        assertThat(result.get(4).getY(), is(1));

        assertThat(result.get(5).getX(), is(2));
        assertThat(result.get(5).getY(), is(1));

        assertThat(result.get(6).getX(), is(0));
        assertThat(result.get(6).getY(), is(0));

        assertThat(result.get(7).getX(), is(1));
        assertThat(result.get(7).getY(), is(0));

        assertThat(result.get(8).getX(), is(2));
        assertThat(result.get(8).getY(), is(0));
    }

    @Test
    public void getPointImplMethods_shouldReturnAllElementsOfCertainTypeWithCorrectCoordinates_ifGivenMapIsRectangleWithDifferentObjects() {
        //given
        String map = "     " +
                "   " + element.ch() + " " +
                "  " + element.ch() + element.ch() + " ";
        int xSize = 5;
        MapParser mapParser = new MapParser(map, xSize);

        //when
        List<PointImpl> result = callTestMethod(mapParser);

        //then
        assertThat(result, hasSize(3));

        assertThat(result.get(0).getX(), is(3));
        assertThat(result.get(0).getY(), is(1));

        assertThat(result.get(1).getX(), is(2));
        assertThat(result.get(1).getY(), is(0));

        assertThat(result.get(2).getX(), is(3));
        assertThat(result.get(2).getY(), is(0));
    }

    @Test
    public void getPointImplMethods_shouldReturnAllElementsOfCertainTypeWithCorrectCoordinates_ifGivenMapIsRectangleWithElementsOfCertainTypeOnly() {
        //given
        String map = "" + element.ch() + element.ch() + element.ch() +
                element.ch() + element.ch() + element.ch();
        int xSize = 3;
        MapParser mapParser = new MapParser(map, xSize);

        //when
        List<PointImpl> result = callTestMethod(mapParser);

        //then
        assertThat(result, hasSize(6));

        assertThat(result.get(0).getX(), is(0));
        assertThat(result.get(0).getY(), is(1));

        assertThat(result.get(1).getX(), is(1));
        assertThat(result.get(1).getY(), is(1));

        assertThat(result.get(2).getX(), is(2));
        assertThat(result.get(2).getY(), is(1));

        assertThat(result.get(3).getX(), is(0));
        assertThat(result.get(3).getY(), is(0));

        assertThat(result.get(4).getX(), is(1));
        assertThat(result.get(4).getY(), is(0));

        assertThat(result.get(5).getX(), is(2));
        assertThat(result.get(5).getY(), is(0));
    }

    private <T extends PointImpl> List<T> callTestMethod(MapParser mapParser) {
        if (element == FENCE) {
            return (List<T>) mapParser.fences();
        } else if (element == ACCELERATOR) {
            return (List<T>) mapParser.accelerators();
        } else if (element == INHIBITOR) {
            return (List<T>) mapParser.inhibitors();
        } else if (element == OBSTACLE) {
            return (List<T>) mapParser.getObstacles();
        } else if (element == LINE_CHANGER_UP) {
            return (List<T>) mapParser.lineUp();
        } else if (element == LINE_CHANGER_DOWN) {
            return (List<T>) mapParser.lineDown();
        } else if (element == SPRINGBOARD_LEFT) {
            return (List<T>) mapParser.dark();
        } else if (element == SPRINGBOARD_RIGHT) {
            return (List<T>) mapParser.light();
        } else if (element == SPRINGBOARD_LEFT_DOWN) {
            return (List<T>) mapParser.leftDown();
        } else if (element == SPRINGBOARD_LEFT_UP) {
            return (List<T>) mapParser.leftUp();
        } else if (element == SPRINGBOARD_RIGHT_DOWN) {
            return (List<T>) mapParser.rightDown();
        } else if (element == SPRINGBOARD_RIGHT_UP) {
            return (List<T>) mapParser.rightUp();
        } else if (element == SPRINGBOARD_TOP) {
            return (List<T>) mapParser.none();
        }
        return null;
    }
}
