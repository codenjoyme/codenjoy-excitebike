package com.codenjoy.dojo.excitebike.model;

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
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static com.codenjoy.dojo.excitebike.TestUtils.parseBikes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class SpawnTest {

    private String init;
    private String expected;
    private int newPlayerNumberAfterInit;
    private GameSettings settings = new TestGameSettings();

    public SpawnTest(String name, int newPlayerNumberAfterInit, String init, String expected) {
        this.newPlayerNumberAfterInit = newPlayerNumberAfterInit;
        this.init = init;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object> data() {
        return Lists.newArrayList(
                new Object[]{"1. shouldAddThreeBikesToFirstColumn",
                        3,
                        "■■■■■■■" +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "Ḃ      \n" +
                        "       \n" +
                        "Ḃ      \n" +
                        "       \n" +
                        "B      \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"2. shouldAddFiveBikesInChessOrder",
                        5,
                        "■■■■■■■" +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "Ḃ      \n" +
                        " Ḃ     \n" +
                        "Ḃ      \n" +
                        " Ḃ     \n" +
                        "B      \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"3. shouldAddSevenBikesInChessOrder",
                        7,
                        "■■■■■■■" +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "Ḃ      \n" +
                        " Ḃ     \n" +
                        "Ḃ Ḃ    \n" +
                        " Ḃ     \n" +
                        "B Ḃ    \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"4. shouldAddTenBikesInChessOrder",
                        10,
                        "■■■■■■■" +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "Ḃ Ḃ    \n" +
                        " Ḃ Ḃ   \n" +
                        "Ḃ Ḃ    \n" +
                        " Ḃ Ḃ   \n" +
                        "B Ḃ    \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"5. shouldAddOneNewBikeBikesToFirstColumn",
                        1,
                        "■■■■■■■" +
                        "       " +
                        "       " +
                        "  B    " +
                        "       " +
                        "   B   " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "       \n" +
                        "       \n" +
                        "  B    \n" +
                        "       \n" +
                        "Ḃ  Ḃ   \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"6. shouldAddThreeNewBikes",
                        3,
                        "■■■■■■■" +
                        "       " +
                        "    B  " +
                        "B      " +
                        "     B " +
                        "   B B " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "Ḃ      \n" +
                        "    B  \n" +
                        "Ḃ      \n" +
                        " Ḃ   Ḃ \n" +
                        "Ḃ  Ḃ Ḃ \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"7. shouldAddThreeBikesInFirstColumn_atSpringboardBeginning",
                        3,
                        "╔════╗■" +
                        "ˊ════ˋ " +
                        "ˊ════ˋ " +
                        "ˊ════ˋ " +
                        "ˊ════ˋ " +
                        "╚ˊˊˊˊ╝ " +
                        "■■■■■■■",
                        "╔════╗■\n" +
                        "Ĺ════ˋ \n" +
                        "ˊ════ˋ \n" +
                        "Ĺ════ˋ \n" +
                        "ˊ════ˋ \n" +
                        "Mˊˊˊˊ╝ \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"8. shouldAddThreeBikesInFirstColumn_atSpringboardTop",
                        3,
                        "════╗■■" +
                        "════ˋ  " +
                        "════ˋ  " +
                        "════ˋ  " +
                        "════ˋ  " +
                        "ˊˊˊˊ╝  " +
                        "■■■■■■■",
                        "Ḃ═══╗■■\n" +
                        "════ˋ  \n" +
                        "Ḃ═══ˋ  \n" +
                        "════ˋ  \n" +
                        "B═══ˋ  \n" +
                        "ˊˊˊˊ╝  \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"9. shouldAddThreeBikesInFirstColumn_atSpringboardEnding",
                        3,
                        "╗■■■■■■" +
                        "ˋ      " +
                        "ˋ      " +
                        "ˋ      " +
                        "ˋ      " +
                        "╝      " +
                        "■■■■■■■",
                        "╗■■■■■■\n" +
                        "Ř      \n" +
                        "ˋ      \n" +
                        "Ř      \n" +
                        "ˋ      \n" +
                        "S      \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"10. shouldAddManyBikes_coveringWholeFieldInChessOrder",
                        18,
                        "■■■■■■■" +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "Ḃ Ḃ Ḃ Ḃ\n" +
                        " Ḃ Ḃ Ḃ \n" +
                        "Ḃ Ḃ Ḃ Ḃ\n" +
                        " Ḃ Ḃ Ḃ \n" +
                        "B Ḃ Ḃ Ḃ\n" +
                        "■■■■■■■\n"
                },
                new Object[]{"11. shouldAddManyBikes_coveringWholeFieldInChessOrderAndTwoMore",
                        20,
                        "■■■■■■■" +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "Ḃ Ḃ Ḃ Ḃ\n" +
                        "ḂḂ Ḃ Ḃ \n" +
                        "Ḃ Ḃ Ḃ Ḃ\n" +
                        "ḂḂ Ḃ Ḃ \n" +
                        "B Ḃ Ḃ Ḃ\n" +
                        "■■■■■■■\n"
                },
                new Object[]{"12. shouldAddManyBikes_fullyCoveringWholeField",
                        35,
                        "■■■■■■■" +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "       " +
                        "■■■■■■■",
                        "■■■■■■■\n" +
                        "ḂḂḂḂḂḂḂ\n" +
                        "ḂḂḂḂḂḂḂ\n" +
                        "ḂḂḂḂḂḂḂ\n" +
                        "ḂḂḂḂḂḂḂ\n" +
                        "BḂḂḂḂḂḂ\n" +
                        "■■■■■■■\n"
                },
                new Object[]{"13. shouldAddManyBikes_coveringWholeSpringboardAndLinesBeforeAndAfterInChessOrder",
                        18,
                        "■╔═══╗■" +
                        " ˊ═══ˋ " +
                        " ˊ═══ˋ " +
                        " ˊ═══ˋ " +
                        " ˊ═══ˋ " +
                        " ╚ˊˊˊ╝ " +
                        "■■■■■■■",
                        "■╔Ḃ═Ḃ╗■\n" +
                        "Ḃˊ═Ḃ═ˋḂ\n" +
                        " ĹḂ═ḂŘ \n" +
                        "Ḃˊ═Ḃ═ˋḂ\n" +
                        " ĹḂ═ḂŘ \n" +
                        "B╚ˊˊˊ╝Ḃ\n" +
                        "■■■■■■■\n"
                },
                new Object[]{"14. shouldAddManyBikes_fullyCoveringWholeSpringboardAndLinesBeforeAndAfter",
                        35,
                        "■╔═══╗■" +
                        " ˊ═══ˋ " +
                        " ˊ═══ˋ " +
                        " ˊ═══ˋ " +
                        " ˊ═══ˋ " +
                        " ╚ˊˊˊ╝ " +
                        "■■■■■■■",
                        "■╔ḂḂḂ╗■\n" +
                        "ḂĹḂḂḂŘḂ\n" +
                        "ḂĹḂḂḂŘḂ\n" +
                        "ḂĹḂḂḂŘḂ\n" +
                        "ḂĹḂḂḂŘḂ\n" +
                        "BṀˊˊˊŜḂ\n" +
                        "■■■■■■■\n"
                }
        );
    }

    @Test
    public void shouldSpawnPlayers() {
        //given
        int xSize = 7;
        MapParser mapParser = new MapParser(init, xSize);

        Dice dice = mock(Dice.class);
        when(dice.next(anyInt())).thenReturn(5);
        Field field = new Excitebike(mapParser, dice, settings);
        PrinterFactory factory = new PrinterFactoryImpl();

        List<Game> games = new ArrayList<>();

        parseBikes(init, xSize).forEach(bike -> {
            Game game = createNewGame(field, factory);
            games.add(game);
            ((Player) game.getPlayer()).setHero(bike);
        });


        //when
        IntStream.range(0, newPlayerNumberAfterInit).mapToObj(i -> createNewGame(field, factory)).forEach(games::add);
        String res = (String) games.get(0).getBoardAsString();

        //then
        assertThat(res, is(expected));
    }

    private Game createNewGame(Field field, PrinterFactory factory) {
        Game game = new Single(new Player(mock(EventListener.class), settings), factory);
        game.on(field);
        game.newGame();
        return game;
    }

}
