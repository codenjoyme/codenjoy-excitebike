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
import com.codenjoy.dojo.games.excitebike.element.GameElement;
import com.codenjoy.dojo.services.dice.MockDice;
import com.codenjoy.dojo.services.printer.CharElement;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@RunWith(Parameterized.class)
public class SingleElementGeneratorParametrizedTest {

    private GameElement expectedElementType;

    public SingleElementGeneratorParametrizedTest(GameElement expectedElementType) {
        this.expectedElementType = expectedElementType;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object> data() {
        return Lists.newArrayList(
                GameElement.ACCELERATOR,
                GameElement.INHIBITOR,
                GameElement.OBSTACLE,
                GameElement.LINE_CHANGER_UP,
                GameElement.LINE_CHANGER_DOWN
        );
    }

    @Test
    public void generate_shouldReturnElementAtLineN_accordingToDice() {
        // given
        MockDice dice = new MockDice();
        int xSize = 10;
        int ySize = 10;
        int expectedLine = new Random().nextInt();
        dice.whenThen(GameElement.values().length - 2,
                Arrays.asList(GameElement.values()).indexOf(expectedElementType) - 2);
        dice.whenThen(ySize - 2,
                expectedLine - 1);

        // when
        Map<? extends CharElement, List<Shiftable>> result = new SingleElementGenerator(dice, xSize, ySize).generate();

        // then
        assertThat(result.values(), hasSize(1));
        assertThat(result.get(expectedElementType), hasSize(1));
        assertThat(result.get(expectedElementType).get(0).getX(), is(xSize - 1));
        assertThat(result.get(expectedElementType).get(0).getY(), is(expectedLine));
    }
}
