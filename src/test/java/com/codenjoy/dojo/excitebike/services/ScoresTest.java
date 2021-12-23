package com.codenjoy.dojo.excitebike.services;

import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.event.ScoresImpl;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.excitebike.services.GameSettings.Keys.LOSE_PENALTY;
import static com.codenjoy.dojo.excitebike.services.GameSettings.Keys.WIN_SCORE;
import static org.junit.Assert.*;

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
        scores = new ScoresImpl<>(0, new Scores(settings));
    }

    @Test
    public void shouldCollectScores() {
        scores = new ScoresImpl<>(100, new Scores(settings));

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
        scores = new ScoresImpl<>(100, new Scores(settings));

        win();
        win();

        assertEquals(100
                    + 2 * settings.integer(WIN_SCORE),
                scores.getScore());
    }

    @Test
    public void shouldLose() {
        scores = new ScoresImpl<>(100, new Scores(settings));

        loose();
        loose();

        assertEquals(100
                    + 2 * settings.integer(LOSE_PENALTY),
                scores.getScore());
    }

    @Test
    public void shouldNotLessThan0() {
        scores = new ScoresImpl<>(1, new Scores(settings));

        loose();
        loose();
        loose();

        assertEquals(0,
                scores.getScore());
    }
}