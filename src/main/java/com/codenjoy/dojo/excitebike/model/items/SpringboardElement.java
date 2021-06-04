package com.codenjoy.dojo.excitebike.model.items;

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

import com.codenjoy.dojo.excitebike.model.Player;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;

public class SpringboardElement extends PointImpl implements State<com.codenjoy.dojo.games.excitebike.element.SpringboardElement, Player>, Shiftable {
    private com.codenjoy.dojo.games.excitebike.element.SpringboardElement currentSpringboardType;

    public SpringboardElement(int x, int y, com.codenjoy.dojo.games.excitebike.element.SpringboardElement type) {
        super(x, y);
        this.currentSpringboardType = type;
    }

    public SpringboardElement(Point point, com.codenjoy.dojo.games.excitebike.element.SpringboardElement type) {
        super(point);
        this.currentSpringboardType = type;
    }

    @Override
    public com.codenjoy.dojo.games.excitebike.element.SpringboardElement state(Player player, Object... objects) {
        return currentSpringboardType;
    }
}
