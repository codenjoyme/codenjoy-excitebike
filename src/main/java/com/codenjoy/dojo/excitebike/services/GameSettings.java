package com.codenjoy.dojo.excitebike.services;

/*-
 * #%L
 * expansion - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 - 2020 Codenjoy
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


import com.codenjoy.dojo.excitebike.services.generation.GenerationOption;
import com.codenjoy.dojo.excitebike.services.generation.WeightedRandomBag;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.excitebike.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.excitebike.services.generation.GenerationOption.*;
import static com.codenjoy.dojo.excitebike.services.generation.GenerationOption.OBSTACLE_CHAIN;

public final class GameSettings extends SettingsImpl implements SettingsReader<GameSettings> {


    public enum Keys implements Key {

        WIN_SCORE("Win score"),
        LOSE_PENALTY("Lose penalty"),
        GENERATION_WEIGHT_NOTHING("Spawn weight: nothing"),
        GENERATION_WEIGHT_SINGLE_ELEMENT("Spawn weight: single element"),
        GENERATION_WEIGHT_SPRINGBOARD("Spawn weight: springboard"),
        GENERATION_WEIGHT_OBSTACLE_CHAIN("Spawn weight: obstacle chain"),
        LEVEL_MAP("Level map");

        private String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    @Override
    public List<Key> allKeys() {
        return Arrays.asList(Keys.values());
    }

    public GameSettings() {
        integer(GENERATION_WEIGHT_NOTHING, 10);
        integer(GENERATION_WEIGHT_SINGLE_ELEMENT, 5);
        integer(GENERATION_WEIGHT_SPRINGBOARD, 2);
        integer(GENERATION_WEIGHT_OBSTACLE_CHAIN, 2);
        integer(WIN_SCORE, 1);
        integer(LOSE_PENALTY, 1);
    }

    public WeightedRandomBag<GenerationOption> getWeightedRandomBag() {
        return new WeightedRandomBag<>(){{
            addEntry(NOTHING, integer(GENERATION_WEIGHT_NOTHING));
            addEntry(SINGLE_ELEMENT, integer(GENERATION_WEIGHT_SINGLE_ELEMENT));
            addEntry(SPRINGBOARD, integer(GENERATION_WEIGHT_SPRINGBOARD));
            addEntry(OBSTACLE_CHAIN, integer(GENERATION_WEIGHT_OBSTACLE_CHAIN));
        }};
    }

}
