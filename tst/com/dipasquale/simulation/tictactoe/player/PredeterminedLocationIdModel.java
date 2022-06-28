package com.dipasquale.simulation.tictactoe.player;

import com.dipasquale.simulation.tictactoe.GameState;

import java.util.Iterator;
import java.util.List;

public final class PredeterminedLocationIdModel implements LocationIdModel {
    private final List<Integer> locationIds;
    private Iterator<Integer> locationIdIterator;

    public PredeterminedLocationIdModel(final List<Integer> locationIds) {
        this.locationIds = locationIds;
        this.locationIdIterator = locationIds.iterator();
    }

    @Override
    public int produceNext(final GameState state) {
        return locationIdIterator.next();
    }

    @Override
    public void restart() {
        locationIdIterator = locationIds.iterator();
    }
}
