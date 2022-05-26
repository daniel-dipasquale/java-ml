package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Edge;

import java.util.concurrent.locks.ReadWriteLock;

public interface ConcurrentEdge extends Edge {
    void acquireSelection();

    int getSelectionCount();

    void releaseSelection();

    ReadWriteLock getLock();
}
