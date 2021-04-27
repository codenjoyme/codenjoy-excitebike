package com.codenjoy.dojo.excitebike.model;

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


import com.codenjoy.dojo.excitebike.model.items.Bike;
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;

public class Player extends GamePlayer<Bike, Field> {

    public Player(EventListener listener, GameSettings settings) {
        super(listener, settings);
    }

    @Override
    public void newHero(Field field) {
        if (hero != null) {  // TODO если закомментировать эти строчки, тесты не ругаются - они тут ннада?
            field.removeFallenBike(hero);
        }

        super.newHero(field);

        // TODO #4e3 потому что игра сама берет на себя создание нового Hero тут
        //      это надо, иначе псоле первого gameOver не будет генериться байк
        //      в рендомном месте. Но надо убрать
        hero.manual(false);
    }

    @Override
    public Bike createHero(Point pt) {
        return new Bike(pt);
    }

    @Override
    public String toString() {
        return "Player{" +
                "bike=" + hero +
                ", listener=" + listener +
                '}';
    }
}
