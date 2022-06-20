package com.dipasquale.simulation.tictactoe;

import java.util.Iterator;
import java.util.List;

public final class ListActionIdModel implements ActionIdModel {
    private final List<Integer> actionIds;
    private Iterator<Integer> actionIdIterator;

    ListActionIdModel(final List<Integer> actionIds) {
        this.actionIds = actionIds;
        this.actionIdIterator = actionIds.iterator();
    }

    @Override
    public int getActionId(final GameState state) {
        return actionIdIterator.next();
    }

    @Override
    public void reset(final GameState state) {
        actionIdIterator = actionIds.iterator();
    }
}
