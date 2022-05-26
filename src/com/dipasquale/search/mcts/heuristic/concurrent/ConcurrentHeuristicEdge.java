package com.dipasquale.search.mcts.heuristic.concurrent;

import com.dipasquale.search.mcts.heuristic.HeuristicEdge;
import com.dipasquale.search.mcts.heuristic.StandardHeuristicEdge;

public final class ConcurrentHeuristicEdge extends AbstractConcurrentHeuristicEdge<HeuristicEdge> implements HeuristicEdge {
    public ConcurrentHeuristicEdge() {
        super(new StandardHeuristicEdge());
    }
}
