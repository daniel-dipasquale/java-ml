package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.core.BackPropagationObserver;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;

public final class ClassicDeterministicBackPropagationPolicy<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> extends AbstractBackPropagationPolicy<TState, ClassicSearchEdge, TEnvironment> {
    public ClassicDeterministicBackPropagationPolicy(final BackPropagationObserver<TState, ClassicSearchEdge, TEnvironment> observer) {
        super(observer);
    }

    @Override
    protected void processCurrent(final SearchNode<TState, ClassicSearchEdge, TEnvironment> leafNode, final int simulationStatusId, final SearchNode<TState, ClassicSearchEdge, TEnvironment> currentNode) {
        ClassicSearchEdge currentEdge = currentNode.getEdge();
        currentEdge.increaseVisited();

        if (currentNode.getState().getParticipantId() == simulationStatusId) {
            currentEdge.increaseWon();
        } else if (simulationStatusId == MonteCarloTreeSearch.DRAWN) {
            currentEdge.increaseDrawn();
        }
    }
}
