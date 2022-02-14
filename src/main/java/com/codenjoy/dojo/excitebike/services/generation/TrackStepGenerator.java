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
import com.codenjoy.dojo.excitebike.services.generation.generator.*;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.printer.CharElement;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.excitebike.services.generation.GenerationOption.*;

public class TrackStepGenerator {

    private final Dice dice;
    private final Map<GenerationOption, Generator> optionGeneratorMap = new EnumMap<>(GenerationOption.class);
    private int generationLock;

    public TrackStepGenerator(Dice dice, int xSize, int ySize) {
        this.dice = dice;
        optionGeneratorMap.put(NOTHING, new EmptyGenerator());
        optionGeneratorMap.put(SINGLE_ELEMENT, new SingleElementGenerator(dice, xSize, ySize));
        optionGeneratorMap.put(SPRINGBOARD, new SpringboardGenerator(dice, xSize, ySize));
        optionGeneratorMap.put(OBSTACLE_CHAIN, new ObstacleChainGenerator(dice, xSize, ySize));
    }

    public Map<? extends CharElement, List<Shiftable>> generate(WeightedRandomBag<GenerationOption> weightedRandomBag) {
        if (generationLock > 0) {
            generationLock--;
            return null;
        }

        GenerationOption generationOption = weightedRandomBag.getRandom(dice);
        Generator generator = optionGeneratorMap.get(generationOption);

        Map<? extends CharElement, List<Shiftable>> generated = generator.generate();
        generationLock = generator.generationLockSize();
        return generated;
    }

}
