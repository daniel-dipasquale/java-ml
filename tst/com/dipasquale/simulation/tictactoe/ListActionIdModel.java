package com.dipasquale.simulation.tictactoe;

import java.util.Iterator;
import java.util.List;

public final class ListActionIdModel implements ActionIdModel {
    private final List<Integer> actionIds;
    private Iterator<Integer> actionIdsIterator;

    ListActionIdModel(final List<Integer> actionIds) {
        this.actionIds = actionIds;
        this.actionIdsIterator = actionIds.iterator();
    }

    @Override
    public int getActionId(final GameState state) {
        return actionIdsIterator.next();
    }

    @Override
    public void reset(final GameResult result) {
        actionIdsIterator = actionIds.iterator();
    }
}
