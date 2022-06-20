package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Edge;

import java.util.concurrent.locks.ReadWriteLock;

public interface ConcurrentEdge extends Edge {
    ReadWriteLock getLock();
}
