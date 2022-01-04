package com.codenjoy.dojo.excitebike.services.parse;

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

import com.codenjoy.dojo.excitebike.model.items.*;
import com.codenjoy.dojo.games.excitebike.element.SpringboardElement;
import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.field.AbstractLevel;

import java.util.List;

import static com.codenjoy.dojo.games.excitebike.element.GameElement.*;
import static com.codenjoy.dojo.games.excitebike.element.SpringboardElement.*;
import static com.codenjoy.dojo.services.PointImpl.pt;

public class MapParser extends AbstractLevel {

    private final int xSize;

    public MapParser(String map, int xSize) {
        super(map);
        this.xSize = xSize;
        this.xy = new LengthToXY(xSize){
            @Override
            public Point getXY(int length) {
                return convertToPoint(length);
            }
        };
    }

    public int width() {
        return xSize;
    }

    public int height() {
        return map.length() / xSize;
    }

    public List<Accelerator> accelerators() {
        return find((pt, el) -> new Accelerator(pt), ACCELERATOR);
    }

    public List<Fence> fences() {
        return find(Fence::new, FENCE);
    }

    public List<Inhibitor> inhibitors() {
        return find((pt, el) -> new Inhibitor(pt), INHIBITOR);
    }

    public List<LineChanger> lineUp() {
        return find((pt, el) -> new LineChanger(pt, true), LINE_CHANGER_UP);
    }

    public List<LineChanger> lineDown() {
        return find((pt, el) -> new LineChanger(pt, false), LINE_CHANGER_DOWN);
    }

    public List<Obstacle> getObstacles() {
        return find((pt, el) -> new Obstacle(pt), OBSTACLE);
    }

    public List<Springboard> dark() {
        return getSpringboard(SPRINGBOARD_LEFT);
    }

    private List<Springboard> getSpringboard(SpringboardElement element) {
        return find(Springboard::new, element);
    }

    public List<Springboard> light() {
        return getSpringboard(SPRINGBOARD_RIGHT);
    }

    public List<Springboard> leftDown() {
        return getSpringboard(SPRINGBOARD_LEFT_DOWN);
    }

    public List<Springboard> leftUp() {
        return getSpringboard(SPRINGBOARD_LEFT_UP);
    }

    public List<Springboard> rightDown() {
        return getSpringboard(SPRINGBOARD_RIGHT_DOWN);
    }

    public List<Springboard> rightUp() {
        return getSpringboard(SPRINGBOARD_RIGHT_UP);
    }

    public List<Springboard> none() {
        return getSpringboard(SPRINGBOARD_TOP);
    }
    
    private Point convertToPoint(int position) {
        if (position == -1) {
            return null;
        }

        return pt(position % xSize,
                (this.map.length() - position - 1) / xSize);
    }
}
