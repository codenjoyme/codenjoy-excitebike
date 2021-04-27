package com.codenjoy.dojo.excitebike.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.excitebike.services.parse.MapParserImpl;
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
    private GameSettings settings = new GameSettings();

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
                new Object[]{"7. shouldAddThreeBikesInFirstColumn__atSpringboardBeginning",
                        3,
                        "╔════╗■" +
                        "/════\\ " +
                        "/════\\ " +
                        "/════\\ " +
                        "/════\\ " +
                        "╚////╝ " +
                        "■■■■■■■",
                        "╔════╗■\n" +
                        "Ĺ════\\ \n" +
                        "/════\\ \n" +
                        "Ĺ════\\ \n" +
                        "/════\\ \n" +
                        "M////╝ \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"8. shouldAddThreeBikesInFirstColumn__atSpringboardTop",
                        3,
                        "════╗■■" +
                        "════\\  " +
                        "════\\  " +
                        "════\\  " +
                        "════\\  " +
                        "////╝  " +
                        "■■■■■■■",
                        "Ḃ═══╗■■\n" +
                        "════\\  \n" +
                        "Ḃ═══\\  \n" +
                        "════\\  \n" +
                        "B═══\\  \n" +
                        "////╝  \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"9. shouldAddThreeBikesInFirstColumn__atSpringboardEnding",
                        3,
                        "╗■■■■■■" +
                        "\\      " +
                        "\\      " +
                        "\\      " +
                        "\\      " +
                        "╝      " +
                        "■■■■■■■",
                        "╗■■■■■■\n" +
                        "Ř      \n" +
                        "\\      \n" +
                        "Ř      \n" +
                        "\\      \n" +
                        "S      \n" +
                        "■■■■■■■\n"
                },
                new Object[]{"10. shouldAddManyBikes__coveringWholeFieldInChessOrder",
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
                new Object[]{"11. shouldAddManyBikes__coveringWholeFieldInChessOrderAndTwoMore",
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
                new Object[]{"12. shouldAddManyBikes__fullyCoveringWholeField",
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
                new Object[]{"13. shouldAddManyBikes__coveringWholeSpringboardAndLinesBeforeAndAfterInChessOrder",
                        18,
                        "■╔═══╗■" +
                        " /═══\\ " +
                        " /═══\\ " +
                        " /═══\\ " +
                        " /═══\\ " +
                        " ╚///╝ " +
                        "■■■■■■■",
                        "■╔Ḃ═Ḃ╗■\n" +
                        "Ḃ/═Ḃ═\\Ḃ\n" +
                        " ĹḂ═ḂŘ \n" +
                        "Ḃ/═Ḃ═\\Ḃ\n" +
                        " ĹḂ═ḂŘ \n" +
                        "B╚///╝Ḃ\n" +
                        "■■■■■■■\n"
                },
                new Object[]{"14. shouldAddManyBikes__fullyCoveringWholeSpringboardAndLinesBeforeAndAfter",
                        35,
                        "■╔═══╗■" +
                        " /═══\\ " +
                        " /═══\\ " +
                        " /═══\\ " +
                        " /═══\\ " +
                        " ╚///╝ " +
                        "■■■■■■■",
                        "■╔ḂḂḂ╗■\n" +
                        "ḂĹḂḂḂŘḂ\n" +
                        "ḂĹḂḂḂŘḂ\n" +
                        "ḂĹḂḂḂŘḂ\n" +
                        "ḂĹḂḂḂŘḂ\n" +
                        "BṀ///ŜḂ\n" +
                        "■■■■■■■\n"
                }
        );
    }

    @Test
    public void shouldSpawnPlayers() {
        //given
        int xSize = 7;
        MapParser mapParser = new MapParserImpl(init, xSize);

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
