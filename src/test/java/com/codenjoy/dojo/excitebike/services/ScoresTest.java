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
import com.codenjoy.dojo.services.event.ScoresMap;
import com.codenjoy.dojo.utils.scorestest.AbstractScoresTest;
import org.junit.Test;

import static com.codenjoy.dojo.excitebike.services.GameSettings.Keys.LOSE_PENALTY;
import static com.codenjoy.dojo.excitebike.services.GameSettings.Keys.WIN_SCORE;

public class ScoresTest extends AbstractScoresTest {

    @Override
    public GameSettings settings() {
        return new TestGameSettings()
                .integer(WIN_SCORE, 1)
                .integer(LOSE_PENALTY, -1);
    }

    @Override
    protected Class<? extends ScoresMap> scores() {
        return Scores.class;
    }

    @Override
    protected Class<? extends Enum> eventTypes() {
        return Event.class;
    }

    @Test
    public void shouldCollectScores() {
        assertEvents("140:\n" +
                "WIN > +1 = 141\n" +
                "WIN > +1 = 142\n" +
                "WIN > +1 = 143\n" +
                "WIN > +1 = 144\n" +
                "LOSE > -1 = 143");
    }

    @Test
    public void shouldWin() {
        // given
        settings.integer(WIN_SCORE, 1);

        // when then
        assertEvents("140:\n" +
                "WIN > +1 = 141\n" +
                "WIN > +1 = 142");
    }

    @Test
    public void shouldLose() {
        // given
        settings.integer(LOSE_PENALTY, -1);

        // when then
        assertEvents("140:\n" +
                "LOSE > -1 = 139\n" +
                "LOSE > -1 = 138");
    }

    @Test
    public void shouldNotLessThan0() {
        assertEvents("1:\n" +
                "LOSE > -1 = 0\n" +
                "LOSE > +0 = 0\n" +
                "LOSE > +0 = 0");
    }

    @Test
    public void shouldClean() {
        assertEvents("140:\n" +
                "WIN > +1 = 141\n" +
                "WIN > +1 = 142\n" +
                "(CLEAN) > -142 = 0\n" +
                "WIN > +1 = 1\n" +
                "WIN > +1 = 2");
    }
}