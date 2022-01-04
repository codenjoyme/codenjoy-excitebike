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

import com.codenjoy.dojo.excitebike.TestGameSettings;
import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.event.Calculator;
import com.codenjoy.dojo.services.event.ScoresImpl;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.excitebike.services.GameSettings.Keys.LOSE_PENALTY;
import static com.codenjoy.dojo.excitebike.services.GameSettings.Keys.WIN_SCORE;
import static org.junit.Assert.assertEquals;

public class ScoresTest {

    private PlayerScores scores;
    private GameSettings settings;

    public void win() {
        scores.event(Event.WIN);
    }

    public void loose() {
        scores.event(Event.LOSE);
    }

    @Before
    public void setup() {
        settings = new TestGameSettings();
    }

    private void givenScores(int score) {
        scores = new ScoresImpl<>(score, new Calculator<>(new Scores(settings)));
    }

    @Test
    public void shouldCollectScores() {
        // given
        givenScores(100);

        // when
        win();
        win();
        win();
        win();
        loose();

        // then
        assertEquals(100
                    + 4 * settings.integer(WIN_SCORE)
                    + settings.integer(LOSE_PENALTY),
                scores.getScore());
    }

    @Test
    public void shouldWin() {
        // given
        givenScores(100);

        // when
        win();
        win();

        // then
        assertEquals(100
                    + 2 * settings.integer(WIN_SCORE),
                scores.getScore());
    }

    @Test
    public void shouldLose() {
        // given
        givenScores(100);

        // when
        loose();
        loose();

        // then
        assertEquals(100
                    + 2 * settings.integer(LOSE_PENALTY),
                scores.getScore());
    }

    @Test
    public void shouldNotLessThan0() {
        // given
        givenScores(1);

        // when
        loose();
        loose();
        loose();

        // then
        assertEquals(0,
                scores.getScore());
    }
}
