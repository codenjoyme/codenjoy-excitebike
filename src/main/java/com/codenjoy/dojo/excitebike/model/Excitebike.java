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
import com.codenjoy.dojo.excitebike.model.items.Fence;
import com.codenjoy.dojo.excitebike.model.items.Shiftable;
import com.codenjoy.dojo.excitebike.services.GameSettings;
import com.codenjoy.dojo.excitebike.services.generation.GenerationOption;
import com.codenjoy.dojo.excitebike.services.generation.TrackStepGenerator;
import com.codenjoy.dojo.excitebike.services.generation.WeightedRandomBag;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.CharElements;

import java.util.*;

import static com.codenjoy.dojo.games.excitebike.element.BikeElement.BIKE_FALLEN;
import static com.codenjoy.dojo.games.excitebike.element.GameElement.*;
import static com.codenjoy.dojo.games.excitebike.element.SpringboardElement.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.toList;


public class Excitebike implements Field {

    private final MapParser mapParser;
    private final Map<CharElements, List<Shiftable>> elements = new HashMap<>();
    private final List<Player> players = new LinkedList<>();
    private final List<Fence> fences;
    private final TrackStepGenerator trackStepGenerator;
    private final GameSettings settings;
    private final Dice dice;

    public Excitebike(MapParser mapParser, Dice dice, GameSettings settings) {
        this.mapParser = mapParser;
        this.settings = settings;
        this.dice = dice;

        fences = mapParser.getFences();

        elements.put(ACCELERATOR, new ArrayList<>(mapParser.getAccelerators()));
        elements.put(INHIBITOR, new ArrayList<>(mapParser.getInhibitors()));
        elements.put(OBSTACLE, new ArrayList<>(mapParser.getObstacles()));
        elements.put(LINE_CHANGER_UP, new ArrayList<>(mapParser.getLineUpChangers()));
        elements.put(LINE_CHANGER_DOWN, new ArrayList<>(mapParser.getLineDownChangers()));
        elements.put(BIKE_FALLEN, new ArrayList<>());

        elements.put(SPRINGBOARD_LEFT_UP, new ArrayList<>(mapParser.getSpringboardLeftUpElements()));
        elements.put(SPRINGBOARD_RIGHT, new ArrayList<>(mapParser.getSpringboardLightElements()));
        elements.put(SPRINGBOARD_LEFT_DOWN, new ArrayList<>(mapParser.getSpringboardLeftDownElements()));
        elements.put(SPRINGBOARD_RIGHT_UP, new ArrayList<>(mapParser.getSpringboardRightUpElements()));
        elements.put(SPRINGBOARD_LEFT, new ArrayList<>(mapParser.getSpringboardDarkElements()));
        elements.put(SPRINGBOARD_RIGHT_DOWN, new ArrayList<>(mapParser.getSpringboardRightDownElements()));
        elements.put(SPRINGBOARD_TOP, new ArrayList<>(mapParser.getSpringboardNoneElements()));

        this.trackStepGenerator = new TrackStepGenerator(dice, mapParser.getXSize(), mapParser.getYSize());
    }

    /**
     * @see Tickable#tick()
     */
    @Override
    public void tick() {
        shiftTrack();
        generateNewTrackStep();

        players.forEach(player -> player.getHero().changeYDependsOnSpringboard());
        players.stream()
                .sorted((o1, o2) -> dice.next(3) - 1)
                .forEach(player -> player.getHero().tick());
        players.forEach(player -> player.getHero().resetTicked());
        // TODO #4e3 тут не надо этого делать, фреймворк сам создаст новый байк, а не игра
        players.stream()
                .filter(player -> player.getHero().getX() < 0)
                .forEach(player -> player.newHero(this));
        elements.put(BIKE_FALLEN, players.stream()
                .map(Player::getHero)
                .filter(h -> h != null && !h.isAlive())
                .collect(toList())
        );
    }

    public int xSize() {
        return mapParser.getXSize();
    }

    @Override
    public int ySize() {
        return mapParser.getYSize();
    }

    @Override
    public boolean isFence(int x, int y) {
        return y < 1 || y > mapParser.getYSize() - 2;
    }

    @Override
    public boolean isInhibitor(int x, int y) {
        return elements.get(INHIBITOR).contains(pt(x, y));
    }

    @Override
    public boolean isAccelerator(int x, int y) {
        return elements.get(ACCELERATOR).contains(pt(x, y));
    }

    @Override
    public boolean isObstacle(int x, int y) {
        return elements.get(OBSTACLE).contains(pt(x, y));
    }

    @Override
    public boolean isUpLineChanger(int x, int y) {
        return elements.get(LINE_CHANGER_UP).contains(pt(x, y));
    }

    @Override
    public boolean isDownLineChanger(int x, int y) {
        return elements.get(LINE_CHANGER_DOWN).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardLeftOrDownElement(int x, int y) {
        return elements.get(SPRINGBOARD_LEFT).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardRightElement(int x, int y) {
        return elements.get(SPRINGBOARD_RIGHT).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardLeftDownElement(int x, int y) {
        return elements.get(SPRINGBOARD_LEFT_DOWN).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardRightDownElement(int x, int y) {
        return elements.get(SPRINGBOARD_RIGHT_DOWN).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardTopElement(int x, int y) {
        return elements.get(SPRINGBOARD_TOP).contains(pt(x, y));
    }

    @Override
    public Optional<Bike> getEnemyBike(int x, int y, Player player) {
        return player != null ?
                players.parallelStream()
                        .filter(p -> p.getHero() != null
                                && !p.equals(player)
                                && p.getHero().itsMe(x, y))
                        .map(Player::getHero)
                        .findFirst()
                : Optional.empty();
    }

    @Override
    public Optional<Point> freeRandom(Player player) {
        return findFreePosition(true)
                .or(() -> findFreePosition(false));
    }

    private Optional<Point> findFreePosition(boolean chessOrder) {
        for (int xi = 0; xi < mapParser.getXSize(); xi++) {
            for (int yi = 1; yi < mapParser.getYSize() - 1; yi++) {
                if (chessOrder && (even(xi) && even(yi) || !even(xi) && !even(yi))) {
                    continue;
                }
                Point lowestPointAtColumn = new PointImpl(xi, 1);
                boolean atSpringboard = pointAtSpringboard(lowestPointAtColumn);
                Point pt = new PointImpl(xi, atSpringboard ? yi + 1 : yi);
                if (isFree(pt)) {
                    return Optional.of(pt);
                }
            }
        }
        return Optional.empty();
    }

    private boolean even(int number) {
        return number % 2 == 0;
    }

    private boolean pointAtSpringboard(Point point) {
        return elements.get(SPRINGBOARD_LEFT).contains(point);
    }

    private boolean isFree(Point point) {
        Point nextPoint = new PointImpl(point.getX()+1, point.getY());
        return !getAliveBikes().contains(point)
                && !fences.contains(point)
                && !fences.contains(nextPoint)
                && !elements.get(OBSTACLE).contains(point)
                && !elements.get(OBSTACLE).contains(nextPoint)
                && !elements.get(BIKE_FALLEN).contains(point)
                && !elements.get(BIKE_FALLEN).contains(nextPoint);
    }

    public List<Bike> getAliveBikes() {
        return players.stream()
                .map(Player::getHero)
                .filter(b -> Objects.nonNull(b) && b.isAlive())
                .collect(toList());
    }

    public List<Fence> getFences() {
        return fences;
    }

    @Override
    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    @Override
    public void remove(Player player) {
        players.remove(player);
    }

    @Override
    public BoardReader reader() {
        return new BoardReader<Player>() {

            @Override
            public int size() {
                return mapParser.getXSize();
            }

            @Override
            public Iterable<? extends Point> elements(Player player) {
                return new LinkedList<Point>() {{
                    addAll(Excitebike.this.getAliveBikes());
                    addAll(Excitebike.this.elements.get(BIKE_FALLEN));
                    Excitebike.this.elements.entrySet()
                            .stream()
                            .filter(e -> e.getKey() != BIKE_FALLEN)
                            .forEach(e -> this.addAll(e.getValue()));
                    addAll(getFences());
                }};
            }
        };
    }

    private void shiftTrack() {
        final int lastPossibleX = 0;
        elements.values().parallelStream().forEach(
                pointsOfElementType -> {
                    pointsOfElementType.forEach(Shiftable::shift);
                    pointsOfElementType.removeIf(point -> point.getX() < lastPossibleX);
                }
        );
    }

    private void generateNewTrackStep() {
        WeightedRandomBag<GenerationOption> weightedRandomBag = settings.getWeightedRandomBag();
        Map<? extends CharElements, List<Shiftable>> generated = trackStepGenerator.generate(weightedRandomBag);
        if (generated != null) {
            generated.forEach((key, elements) -> this.elements.merge(key, elements, (currentElements, newElements) -> {
                        currentElements.addAll(newElements);
                        return currentElements;
                    }
            ));
        }
    }

    @Override
    public Player getPlayerOfBike(Bike bike) {
        return players.parallelStream()
                .filter(p -> p.getHero() == bike)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void removeFallenBike(Bike bike) {
        elements.get(BIKE_FALLEN).remove(bike);
    }

    @Override
    public GameSettings settings() {
        return settings;
    }
}