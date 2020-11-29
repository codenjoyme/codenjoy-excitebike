package com.codenjoy.dojo.excitebike.services.parse;

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

import com.codenjoy.dojo.excitebike.model.elements.SpringboardElementType;
import com.codenjoy.dojo.excitebike.model.items.Accelerator;
import com.codenjoy.dojo.excitebike.model.items.Fence;
import static com.codenjoy.dojo.excitebike.model.elements.GameElementType.*;
import com.codenjoy.dojo.excitebike.model.items.Inhibitor;
import com.codenjoy.dojo.excitebike.model.items.LineChanger;
import com.codenjoy.dojo.excitebike.model.items.Obstacle;
import com.codenjoy.dojo.excitebike.model.items.SpringboardElement;
import static com.codenjoy.dojo.excitebike.model.elements.SpringboardElementType.*;
import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.utils.LevelUtils;

import java.util.List;

public class MapParserImpl implements MapParser {

    private String map;
    private int xSize;
    private LengthToXY xy;

    public MapParserImpl(String map, int xSize) {
        this.map = map;
        this.xSize = xSize;
        this.xy = new LengthToXY(xSize){
            @Override
            public Point getXY(int length) {
                return convertToPoint(length);
            }
        };
    }

    @Override
    public int getXSize() {
        return xSize;
    }

    @Override
    public int getYSize() {
        return map.length() / xSize;
    }

    @Override
    public List<Accelerator> getAccelerators() {
        return LevelUtils.getObjects(xy, map,
                (pt, el) -> new Accelerator(pt),
                ACCELERATOR);
    }

    @Override
    public List<Fence> getFences() {
        return LevelUtils.getObjects(xy, map,
                (pt, el) -> new Fence(pt),
                FENCE);
    }

    @Override
    public List<Inhibitor> getInhibitors() {
        return LevelUtils.getObjects(xy, map,
                (pt, el) -> new Inhibitor(pt), 
                INHIBITOR);
    }

    @Override
    public List<LineChanger> getLineUpChangers() {
        return LevelUtils.getObjects(xy, map,
                (pt, el) -> new LineChanger(pt, true), 
                LINE_CHANGER_UP);
    }

    @Override
    public List<LineChanger> getLineDownChangers() {
        return LevelUtils.getObjects(xy, map,
                (pt, el) -> new LineChanger(pt, false), 
                LINE_CHANGER_DOWN);
    }

    @Override
    public List<Obstacle> getObstacles() {
        return LevelUtils.getObjects(xy, map,
                (pt, el) -> new Obstacle(pt), 
                OBSTACLE);
    }


    @Override
    public List<SpringboardElement> getSpringboardDarkElements() {
        return getSpringboard(SPRINGBOARD_LEFT);
    }

    private List<SpringboardElement> getSpringboard(SpringboardElementType element) {
        return LevelUtils.getObjects(xy, map,
                (pt, el) -> new SpringboardElement(pt, el),
                element);
    }

    @Override
    public List<SpringboardElement> getSpringboardLightElements() {
        return getSpringboard(SPRINGBOARD_RIGHT);
    }

    @Override
    public List<SpringboardElement> getSpringboardLeftDownElements() {
        return getSpringboard(SPRINGBOARD_LEFT_DOWN);
    }

    @Override
    public List<SpringboardElement> getSpringboardLeftUpElements() {
        return getSpringboard(SPRINGBOARD_LEFT_UP);
    }

    @Override
    public List<SpringboardElement> getSpringboardRightDownElements() {
        return getSpringboard(SPRINGBOARD_RIGHT_DOWN);
    }

    @Override
    public List<SpringboardElement> getSpringboardRightUpElements() {
        return getSpringboard(SPRINGBOARD_RIGHT_UP);
    }

    @Override
    public List<SpringboardElement> getSpringboardNoneElements() {
        return getSpringboard(SPRINGBOARD_TOP);
    }
    
    private Point convertToPoint(int position) {
        return position == -1
                ? null
                : PointImpl.pt(position % xSize, (this.map.length() - position - 1) / xSize);
    }
}
