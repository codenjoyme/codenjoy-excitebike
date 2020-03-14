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
import com.codenjoy.dojo.excitebike.client.Board;
import com.codenjoy.dojo.excitebike.ai.AISolver;
import com.codenjoy.dojo.excitebike.model.GameFieldImpl;
import com.codenjoy.dojo.excitebike.model.Player;
import com.codenjoy.dojo.excitebike.model.elements.GameElementType;
import com.codenjoy.dojo.excitebike.model.elements.BikeType;
import com.codenjoy.dojo.excitebike.model.elements.SpringboardElementType;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.excitebike.services.parse.MapParserImpl;
import com.codenjoy.dojo.services.AbstractGameType;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.GameType;
import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.MultiplayerType;
import com.codenjoy.dojo.services.printer.CharElements;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.google.common.collect.ObjectArrays;

import static com.codenjoy.dojo.services.settings.SimpleParameter.v;

public class GameRunner extends AbstractGameType implements GameType {

    //-----------------------------------
    // Don't forget to adjust excitebike.js if you change those x/y parameters:
    private static final int X_SIZE = 30;
    private static final int Y_SIZE = 12;
    //-----------------------------------
    private final MapParser mapParser;
    private SettingsHandler settingsHandler;

    public GameRunner() {
        mapParser = new MapParserImpl(getMap(), X_SIZE);
    }

    protected String getMap() {
        StringBuilder sb = new StringBuilder();
        appendElementForWholeLine(sb, GameElementType.FENCE);
        for (int i = 0; i < Y_SIZE - 2; i++) {
            appendElementForWholeLine(sb, GameElementType.NONE);
        }
        appendElementForWholeLine(sb, GameElementType.FENCE);
        return sb.toString();
    }

    private void appendElementForWholeLine(StringBuilder sb, GameElementType element) {
        for (int i = 0; i < GameRunner.X_SIZE; i++) {
            sb.append(element);
        }
    }

    @Override
    public PlayerScores getPlayerScores(Object score) {
        return new Scores((Integer) score, settingsHandler);
    }

    @Override
    public GameField createGame(int levelNumber) {
        return new GameFieldImpl(mapParser, getDice(), settingsHandler);
    }

    @Override
    protected SettingsImpl createSettings() {
        settingsHandler = new SettingsHandler();
        return settingsHandler.getSettings();
    }

    @Override
    public Parameter<Integer> getBoardSize() {
        return v(X_SIZE);
    }

    @Override
    public String name() {
        return "excitebike";
    }

    @Override
    public CharElements[] getPlots() {
        CharElements[] result = ObjectArrays.concat(GameElementType.values(), SpringboardElementType.values(), CharElements.class);
        result = ObjectArrays.concat(result, BikeType.values(), CharElements.class);
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
    public MultiplayerType getMultiplayerType() {
        return MultiplayerType.MULTIPLE;
    }

    @Override
    public GamePlayer createPlayer(EventListener listener, String playerName) {
        Player player = new Player(listener, playerName);
        return player;
    }
}
