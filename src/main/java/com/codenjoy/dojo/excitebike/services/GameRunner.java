package com.codenjoy.dojo.excitebike.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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


import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.games.excitebike.Board;
import com.codenjoy.dojo.excitebike.services.ai.AISolver;
import com.codenjoy.dojo.excitebike.model.Excitebike;
import com.codenjoy.dojo.excitebike.model.Player;
import com.codenjoy.dojo.games.excitebike.element.GameElement;
import com.codenjoy.dojo.games.excitebike.element.BikeElement;
import com.codenjoy.dojo.games.excitebike.element.SpringboardElement;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.excitebike.services.parse.MapParserImpl;
import com.codenjoy.dojo.services.AbstractGameType;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.MultiplayerType;
import com.codenjoy.dojo.services.printer.CharElement;
import com.codenjoy.dojo.services.settings.Parameter;
import com.google.common.collect.ObjectArrays;

import static com.codenjoy.dojo.services.settings.SimpleParameter.v;

public class GameRunner extends AbstractGameType<GameSettings> {

    //-----------------------------------
    // Don't forget to adjust excitebike.js if you change those x/y parameters:
    private static final int X_SIZE = 30;
    private static final int Y_SIZE = 12;
    //-----------------------------------
    private final MapParser mapParser;

    @Override
    public GameSettings getSettings() {
        return new GameSettings();
    }

    public GameRunner() {
        mapParser = new MapParserImpl(getMap(), X_SIZE);
    }

    protected String getMap() {
        StringBuilder sb = new StringBuilder();
        appendElementForWholeLine(sb, GameElement.FENCE);
        for (int i = 0; i < Y_SIZE - 2; i++) {
            appendElementForWholeLine(sb, GameElement.NONE);
        }
        appendElementForWholeLine(sb, GameElement.FENCE);
        return sb.toString();
    }

    private void appendElementForWholeLine(StringBuilder sb, GameElement element) {
        for (int i = 0; i < GameRunner.X_SIZE; i++) {
            sb.append(element);
        }
    }

    @Override
    public PlayerScores getPlayerScores(Object score, GameSettings settings) {
        return new Scores(Integer.valueOf(score.toString()), settings);
    }

    @Override
    public GameField createGame(int levelNumber, GameSettings settings) {
        return new Excitebike(mapParser, getDice(), settings);
    }

    @Override
    public Parameter<Integer> getBoardSize(GameSettings settings) {
        return v(X_SIZE);
    }

    @Override
    public String name() {
        return "excitebike";
    }

    @Override
    public CharElement[] getPlots() {
        CharElement[] result = ObjectArrays.concat(GameElement.values(), SpringboardElement.values(), CharElement.class);
        result = ObjectArrays.concat(result, BikeElement.values(), CharElement.class);
        return result;
    }

    @Override
    public Class<? extends Solver> getAI() {
        return AISolver.class;
    }

    @Override
    public Class<? extends ClientBoard> getBoard() {
        return Board.class;
    }

    @Override
    public MultiplayerType getMultiplayerType(GameSettings settings) {
        return MultiplayerType.MULTIPLE;
    }

    @Override
    public GamePlayer createPlayer(EventListener listener, String playerId, GameSettings settings) {
        return new Player(listener, settings);
    }
}
