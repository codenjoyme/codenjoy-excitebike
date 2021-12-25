package com.codenjoy.dojo.excitebike.services;

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
        settings = new GameSettings();
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