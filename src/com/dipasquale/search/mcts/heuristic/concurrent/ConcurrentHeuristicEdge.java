package com.dipasquale.search.mcts.heuristic.concurrent;

import com.dipasquale.search.mcts.heuristic.HeuristicEdge;
import com.dipasquale.search.mcts.heuristic.StandardHeuristicEdge;

import java.util.concurrent.locks.ReadWriteLock;

public final class ConcurrentHeuristicEdge extends AbstractConcurrentHeuristicEdge<HeuristicEdge> implements HeuristicEdge {
    private final StandardHeuristicEdge edge;

    private ConcurrentHeuristicEdge(final StandardHeuristicEdge edge, final ReadWriteLock lock) {
        super(edge, lock);
        this.edge = edge;
    }

    public ConcurrentHeuristicEdge(final ReadWriteLock lock) {
        this(new StandardHeuristicEdge(), lock);
    }

    public ConcurrentHeuristicEdge(final ConcurrentHeuristicEdge edge) {
        this(new StandardHeuristicEdge(edge.edge), edge.getLock());
    }
}
