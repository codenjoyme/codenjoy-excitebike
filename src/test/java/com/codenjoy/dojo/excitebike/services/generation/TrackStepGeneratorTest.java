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

import com.codenjoy.dojo.excitebike.model.items.Shiftable;
import com.codenjoy.dojo.excitebike.services.generation.generator.SpringboardGenerator;
import com.codenjoy.dojo.services.dice.MockDice;
import com.codenjoy.dojo.services.printer.CharElement;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.excitebike.services.generation.GenerationOption.*;
import static com.codenjoy.dojo.games.excitebike.Element.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;

public class TrackStepGeneratorTest {

    private MockDice dice = new MockDice();
    
    @Test
    public void generate_shouldReturnElements_ifDiceReturnedNumberCorrespondingToSpringboardAmongOtherOptions() {
        // given
        int xSize = 10;
        int ySize = 10;
        WeightedRandomBag<GenerationOption> weightedRandomBag = getWeightedRandomGenerationOptionBag();
        TrackStepGenerator generator = new TrackStepGenerator(dice, xSize, ySize);
        dice.whenThen(19, 16);
        dice.whenThen(SpringboardGenerator.SPRINGBOARD_TOP_MAX_WIDTH, 3);

        // when
        Map<? extends CharElement, List<Shiftable>> result = generator.generate(weightedRandomBag);

        // then
        assertThat(result.values(), hasSize(7));
        assertThat(result.get(SPRINGBOARD_LEFT_UP), hasSize(1));
        assertThat(result.get(SPRINGBOARD_LEFT), hasSize(10));
        assertThat(result.get(SPRINGBOARD_LEFT_DOWN), hasSize(1));
        assertThat(result.get(SPRINGBOARD_TOP), hasSize(24));
        assertThat(result.get(SPRINGBOARD_RIGHT_UP), hasSize(1));
        assertThat(result.get(SPRINGBOARD_RIGHT), hasSize(7));
        assertThat(result.get(SPRINGBOARD_RIGHT_DOWN), hasSize(1));
    }

    @Test
    public void generate_shouldReturnNullSevenTimes_afterItReturnedSpringboardWithWidthFive() {
        // given
        int xSize = 10;
        int ySize = 10;
        WeightedRandomBag<GenerationOption> weightedRandomBag = getWeightedRandomGenerationOptionBag();
        TrackStepGenerator generator = new TrackStepGenerator(dice, xSize, ySize);
        dice.whenThen(19, 16, 12);
        dice.whenThen(SpringboardGenerator.SPRINGBOARD_TOP_MAX_WIDTH, 3);

        // when
        Map<? extends CharElement, List<Shiftable>> result = generator.generate(weightedRandomBag);

        // then
        assertThat(result.values(), hasSize(7));
        assertThat(result.get(SPRINGBOARD_LEFT_UP), hasSize(1));
        assertThat(result.get(SPRINGBOARD_LEFT), hasSize(10));
        assertThat(result.get(SPRINGBOARD_LEFT_DOWN), hasSize(1));
        assertThat(result.get(SPRINGBOARD_TOP), hasSize(24));
        assertThat(result.get(SPRINGBOARD_RIGHT_UP), hasSize(1));
        assertThat(result.get(SPRINGBOARD_RIGHT), hasSize(7));
        assertThat(result.get(SPRINGBOARD_RIGHT_DOWN), hasSize(1));
        for (int i = 1; i <= 7; i++) {
            assertThat(generator.generate(weightedRandomBag), nullValue());
        }
        assertThat(generator.generate(weightedRandomBag).values(), hasSize(1));
    }

    @Test
    public void generate_shouldReturnNull_ifDiceReturnedNumberCorrespondingToNothingAmongOtherOptions() {
        // given
        int xSize = 10;
        int ySize = 10;
        WeightedRandomBag<GenerationOption> weightedRandomBag = getWeightedRandomGenerationOptionBag();
        TrackStepGenerator generator = new TrackStepGenerator(dice, xSize, ySize);
        dice.whenThen(19, 5);

        // when
        Map<? extends CharElement, List<Shiftable>> result = generator.generate(weightedRandomBag);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void generate_shouldReturnMapWithSingleElement_ifDiceReturnedNumberCorrespondingToSingleElementAmongOtherOptions() {
        // given
        int xSize = 10;
        int ySize = 10;
        WeightedRandomBag<GenerationOption> weightedRandomBag = getWeightedRandomGenerationOptionBag();
        TrackStepGenerator generator = new TrackStepGenerator(dice, xSize, ySize);
        dice.whenThen(19, 12);

        // when
        Map<? extends CharElement, List<Shiftable>> result = generator.generate(weightedRandomBag);

        // then
        assertThat(result.values(), hasSize(1));
        assertThat(generator.generate(weightedRandomBag).values(), hasSize(1));
        assertThat(result.get(ACCELERATOR), hasSize(1));
    }

    @Test
    public void generate_shouldReturnMapWithObstacleChain_ifDiceReturnedNumberCorrespondingToObstacleChainAmongOtherOptions() {
        // given
        int xSize = 10;
        int ySize = 10;
        WeightedRandomBag<GenerationOption> weightedRandomBag = getWeightedRandomGenerationOptionBag();
        TrackStepGenerator generator = new TrackStepGenerator(dice, xSize, ySize);
        dice.whenThen(19, 18);

        // when
        Map<? extends CharElement, List<Shiftable>> result = generator.generate(weightedRandomBag);

        // then
        assertThat(result.values(), hasSize(1));
        assertThat(result.get(OBSTACLE).size(), greaterThan(0));
    }

    private WeightedRandomBag<GenerationOption> getWeightedRandomGenerationOptionBag() {
        WeightedRandomBag<GenerationOption> weightedRandomBag = new WeightedRandomBag<>();
        weightedRandomBag.addEntry(NOTHING, 10);
        weightedRandomBag.addEntry(SINGLE_ELEMENT, 5);
        weightedRandomBag.addEntry(SPRINGBOARD, 2);
        weightedRandomBag.addEntry(OBSTACLE_CHAIN, 1);
        return weightedRandomBag;
    }
}
