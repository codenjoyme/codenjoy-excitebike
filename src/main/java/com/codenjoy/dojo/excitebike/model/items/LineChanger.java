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

import com.codenjoy.dojo.excitebike.model.Player;
import com.codenjoy.dojo.games.excitebike.element.GameElement;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.printer.state.State;

public class LineChanger extends PointImpl implements State<GameElement, Player>, Shiftable {
    private boolean upper;

    public LineChanger(int x, int y, boolean upper) {
        super(x, y);
        this.upper = upper;
    }

    public LineChanger(Point point, boolean upper) {
        super(point);
        this.upper = upper;
    }

    public boolean isUpper() {
        return upper;
    }

    @Override
    public GameElement state(Player player, Object... objects) {
        return isUpper() ? GameElement.LINE_CHANGER_UP : GameElement.LINE_CHANGER_DOWN;
    }
}
