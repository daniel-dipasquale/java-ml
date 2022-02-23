package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Edge;

@FunctionalInterface
public interface ProposalStrategy<T extends Edge> {
    float calculateEfficiency(int simulations, T edge);
}
