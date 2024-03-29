package com.codenjoy.dojo.excitebike.services;

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

import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameSettingsTest {

    @Test
    public void shouldGetAllKeys() {
        assertEquals("WIN_SCORE                        =[Score] Win score\n" +
                    "LOSE_PENALTY                     =[Score] Lose penalty\n" +
                    "GENERATION_WEIGHT_NOTHING        =[Level] Spawn weight: nothing\n" +
                    "GENERATION_WEIGHT_SINGLE_ELEMENT =[Level] Spawn weight: single element\n" +
                    "GENERATION_WEIGHT_SPRINGBOARD    =[Level] Spawn weight: springboard\n" +
                    "GENERATION_WEIGHT_OBSTACLE_CHAIN =[Level] Spawn weight: obstacle chain\n" +
                    "LEVEL_MAP                        =[Level] Level map",
                TestUtils.toString(new GameSettings().allKeys()));
    }
}