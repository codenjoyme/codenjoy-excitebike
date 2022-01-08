package com.codenjoy.dojo.excitebike.model.items;

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
import com.codenjoy.dojo.excitebike.model.Excitebike;
import com.codenjoy.dojo.excitebike.model.Player;
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.games.excitebike.element.GameElement;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.printer.state.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class SpawnElementsSystemParameterizedTest {
    private GameElement gameElementType;
    private Player player;
    private Excitebike game;
    private Dice dice;

    public SpawnElementsSystemParameterizedTest(GameElement gameElementType) {
        this.gameElementType = gameElementType;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection data() {
        return Arrays.stream(GameElement.values(), 2, GameElement.values().length).collect(toList());
    }

    @Before
    public void init() {
        dice = mock(Dice.class);
        MapParser mapParser = mock(MapParser.class);
        when(mapParser.width()).thenReturn(5);
        when(mapParser.height()).thenReturn(5);
        GameSettings settings = new TestGameSettings();
        game = new Excitebike(mapParser, dice, settings);
        player = new Player(mock(EventListener.class), settings);
        game.newGame(player);
    }

    @Test
    public void shouldGenerateElement() {
        //given
        when(dice.next(20)).thenReturn(12);
        when(dice.next(5)).thenReturn(gameElementType.ordinal() - 2);
        when(dice.next(3)).thenReturn(0);

        //when
        game.tick();

        //then
        LinkedList<Object> all = new LinkedList<>();
        game.reader().addAll(null, list -> all.addAll((Collection<?>) list));
        PointImpl generatedElement = (PointImpl)all.stream().filter(el -> !(el instanceof Bike || el instanceof Fence)).findFirst().get();
        assertThat(((State) generatedElement).state(player), is(gameElementType));

    }
}
