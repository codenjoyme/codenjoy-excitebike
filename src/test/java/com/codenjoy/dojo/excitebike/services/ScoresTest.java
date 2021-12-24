package com.codenjoy.dojo.excitebike.services;

import com.codenjoy.dojo.services.PlayerScores;
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
        settings = new GameSettings();
        givenScores(0);
    }

    private void givenScores(int score) {
        scores = new ScoresImpl<>(score, new Scores(settings));
    }

    @Test
    public void shouldCollectScores() {
        givenScores(100);

        win();
        win();
        win();
        win();
        loose();

        assertEquals(100
                    + 4 * settings.integer(WIN_SCORE)
                    + settings.integer(LOSE_PENALTY),
                scores.getScore());
    }

    @Test
    public void shouldWin() {
        givenScores(100);

        win();
        win();

        assertEquals(100
                    + 2 * settings.integer(WIN_SCORE),
                scores.getScore());
    }

    @Test
    public void shouldLose() {
        givenScores(100);

        loose();
        loose();

        assertEquals(100
                    + 2 * settings.integer(LOSE_PENALTY),
                scores.getScore());
    }

    @Test
    public void shouldNotLessThan0() {
        givenScores(1);

        loose();
        loose();
        loose();

        assertEquals(0,
                scores.getScore());
    }
}