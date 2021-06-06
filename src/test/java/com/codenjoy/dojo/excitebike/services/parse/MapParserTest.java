package com.codenjoy.dojo.excitebike.services.parse;

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

import com.codenjoy.dojo.games.excitebike.element.GameElement;
import com.codenjoy.dojo.games.excitebike.element.SpringboardElement;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.printer.CharElement;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * Created by Pavel Bobylev 6/4/2019
 */
@RunWith(Parameterized.class)
public class MapParserTest {

    private CharElement element;

    public MapParserTest(CharElement element) {
        this.element = element;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection data() {
        return Lists.newArrayList(
                GameElement.FENCE,
                GameElement.ACCELERATOR,
                GameElement.INHIBITOR,
                GameElement.OBSTACLE,
                GameElement.LINE_CHANGER_UP,
                GameElement.LINE_CHANGER_DOWN,
                SpringboardElement.SPRINGBOARD_LEFT,
                SpringboardElement.SPRINGBOARD_RIGHT,
                SpringboardElement.SPRINGBOARD_LEFT_DOWN,
                SpringboardElement.SPRINGBOARD_RIGHT_DOWN,
                SpringboardElement.SPRINGBOARD_LEFT_UP,
                SpringboardElement.SPRINGBOARD_LEFT_DOWN,
                SpringboardElement.SPRINGBOARD_TOP
        );
    }

    @Test
    public void getPointImplMethods__shouldReturnAllElementsOfCertainTypeWithCorrectCoordinates__ifGivenMapIsSquareWithDifferentObjects() {
        //given
        String map = "     " +
                "   " + element.ch() + " " +
                "  " + element.ch() + element.ch() + " " +
                "     " +
                element.ch() + "    ";
        int xSize = 5;
        MapParserImpl mapParser = new MapParserImpl(map, xSize);

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
    public void getPointImplMethods__shouldReturnAllElementsOfCertainTypeWithCorrectCoordinates__ifGivenMapIsSquareWithElementsOfCertainTypeOnly() {
        //given
        String map = "" + element.ch() + element.ch() + element.ch() +
                element.ch() + element.ch() + element.ch() +
                element.ch() + element.ch() + element.ch();
        int xSize = 3;
        MapParserImpl mapParser = new MapParserImpl(map, xSize);

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
    public void getPointImplMethods__shouldReturnAllElementsOfCertainTypeWithCorrectCoordinates__ifGivenMapIsRectangleWithDifferentObjects() {
        //given
        String map = "     " +
                "   " + element.ch() + " " +
                "  " + element.ch() + element.ch() + " ";
        int xSize = 5;
        MapParserImpl mapParser = new MapParserImpl(map, xSize);

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
    public void getPointImplMethods__shouldReturnAllElementsOfCertainTypeWithCorrectCoordinates__ifGivenMapIsRectangleWithElementsOfCertainTypeOnly() {
        //given
        String map = "" + element.ch() + element.ch() + element.ch() +
                element.ch() + element.ch() + element.ch();
        int xSize = 3;
        MapParserImpl mapParser = new MapParserImpl(map, xSize);

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

    private <T extends PointImpl> List<T> callTestMethod(MapParserImpl mapParser) {
        if (element == GameElement.FENCE) {
            return (List<T>) mapParser.fences();
        } else if (element == GameElement.ACCELERATOR) {
            return (List<T>) mapParser.accelerators();
        } else if (element == GameElement.INHIBITOR) {
            return (List<T>) mapParser.inhibitors();
        } else if (element == GameElement.OBSTACLE) {
            return (List<T>) mapParser.getObstacles();
        } else if (element == GameElement.LINE_CHANGER_UP) {
            return (List<T>) mapParser.lineUp();
        } else if (element == GameElement.LINE_CHANGER_DOWN) {
            return (List<T>) mapParser.lineDown();
        } else if (element == SpringboardElement.SPRINGBOARD_LEFT) {
            return (List<T>) mapParser.dark();
        } else if (element == SpringboardElement.SPRINGBOARD_RIGHT) {
            return (List<T>) mapParser.light();
        } else if (element == SpringboardElement.SPRINGBOARD_LEFT_DOWN) {
            return (List<T>) mapParser.leftDown();
        } else if (element == SpringboardElement.SPRINGBOARD_LEFT_UP) {
            return (List<T>) mapParser.leftUp();
        } else if (element == SpringboardElement.SPRINGBOARD_RIGHT_DOWN) {
            return (List<T>) mapParser.rightDown();
        } else if (element == SpringboardElement.SPRINGBOARD_RIGHT_UP) {
            return (List<T>) mapParser.rightUp();
        } else if (element == SpringboardElement.SPRINGBOARD_TOP) {
            return (List<T>) mapParser.none();
        }
        return null;
    }
}
