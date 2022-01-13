package com.codenjoy.dojo.excitebike.services.generation;

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

import com.codenjoy.dojo.services.dice.MockDice;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class WeightedRandomBagTest {

    @Test
    public void getRandom_shouldReturnNull_ifNoOptionsWereSet() {
        // given
        WeightedRandomBag<GenerationOption> bag = new WeightedRandomBag<>();
        MockDice dice = new MockDice();
        dice.whenThen(1, 0);

        // when
        GenerationOption result = bag.getRandom(dice);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void getRandom_shouldReturnTheOnlyOptionSet_ifItIsTheOnlyOptionAndDiceReturnsCorrespondingWeight() {
        // given
        WeightedRandomBag<GenerationOption> bag = new WeightedRandomBag<>();
        bag.addEntry(GenerationOption.NOTHING, 50);
        MockDice dice = new MockDice();
        dice.whenThen(51, 25);

        // when
        GenerationOption result = bag.getRandom(dice);

        // then
        assertThat(result, is(GenerationOption.NOTHING));
    }

    @Test
    public void getRandom_shouldReturnOptionOne_ifTwoOptionsWereSetAndDiceReturnsWeightCorrespondingToOptionOne() {
        // given
        WeightedRandomBag<GenerationOption> bag = new WeightedRandomBag<>();
        bag.addEntry(GenerationOption.NOTHING, 50);
        bag.addEntry(GenerationOption.OBSTACLE_CHAIN, 32);
        MockDice dice = new MockDice();
        dice.whenThen(84, 1);

        // when
        GenerationOption result = bag.getRandom(dice);

        // then
        assertThat(result, is(GenerationOption.NOTHING));
    }

    @Test
    public void getRandom_shouldReturnOptionTwo_ifTwoOptionsWereSetAndDiceReturnsWeightCorrespondingToOptionTwo() {
        // given
        WeightedRandomBag<GenerationOption> bag = new WeightedRandomBag<>();
        bag.addEntry(GenerationOption.NOTHING, 50);
        bag.addEntry(GenerationOption.OBSTACLE_CHAIN, 27);
        MockDice dice = new MockDice();
        dice.whenThen(78, 58);

        // when
        GenerationOption result = bag.getRandom(dice);

        // then
        assertThat(result, is(GenerationOption.OBSTACLE_CHAIN));
    }

    @Test
    public void getRandom_shouldReturnOptionOne_ifThreeOptionsWereSetAndDiceReturnsWeightCorrespondingToOptionOne() {
        // given
        WeightedRandomBag<GenerationOption> bag = new WeightedRandomBag<>();
        bag.addEntry(GenerationOption.SPRINGBOARD, 50);
        bag.addEntry(GenerationOption.SINGLE_ELEMENT, 32);
        bag.addEntry(GenerationOption.OBSTACLE_CHAIN, 111);
        MockDice dice = new MockDice();
        dice.whenThen(194, 1);

        // when
        GenerationOption result = bag.getRandom(dice);

        // then
        assertThat(result, is(GenerationOption.SPRINGBOARD));
    }

    @Test
    public void getRandom_shouldReturnOptionTwo_ifThreeOptionsWereSetAndDiceReturnsWeightCorrespondingToOptionTwo() {
        // given
        WeightedRandomBag<GenerationOption> bag = new WeightedRandomBag<>();
        bag.addEntry(GenerationOption.SPRINGBOARD, 50);
        bag.addEntry(GenerationOption.SINGLE_ELEMENT, 32);
        bag.addEntry(GenerationOption.OBSTACLE_CHAIN, 111);
        MockDice dice = new MockDice();
        dice.whenThen(194, 75);

        // when
        GenerationOption result = bag.getRandom(dice);

        // then
        assertThat(result, is(GenerationOption.SINGLE_ELEMENT));
    }

    @Test
    public void getRandom_shouldReturnOptionThree_ifThreeOptionsWereSetAndDiceReturnsWeightCorrespondingToOptionThree() {
        // given
        WeightedRandomBag<GenerationOption> bag = new WeightedRandomBag<>();
        bag.addEntry(GenerationOption.SPRINGBOARD, 50);
        bag.addEntry(GenerationOption.SINGLE_ELEMENT, 32);
        bag.addEntry(GenerationOption.OBSTACLE_CHAIN, 111);
        MockDice dice = new MockDice();
        dice.whenThen(194, 184);

        // when
        GenerationOption result = bag.getRandom(dice);

        // then
        assertThat(result, is(GenerationOption.OBSTACLE_CHAIN));
    }
}