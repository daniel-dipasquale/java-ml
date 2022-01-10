package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.core.BackPropagationObserver;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

public final class ClassicBackPropagationPolicy<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> extends AbstractBackPropagationPolicy<TState, ClassicEdge, TEnvironment> {
    public ClassicBackPropagationPolicy(final BackPropagationObserver<TState, ClassicEdge, TEnvironment> observer) {
        super(observer);
    }

    @Override
    protected void process(final SearchNode<TState, ClassicEdge, TEnvironment> leafNode, final int simulationStatusId, final SearchNode<TState, ClassicEdge, TEnvironment> currentNode) {
        ClassicEdge currentEdge = currentNode.getEdge();

        currentEdge.increaseVisited();

        if (currentNode.getState().getParticipantId() == simulationStatusId) {
            currentEdge.increaseWon();
        } else if (simulationStatusId == MonteCarloTreeSearch.DRAWN) {
            currentEdge.increaseDrawn();
        } else if (simulationStatusId == MonteCarloTreeSearch.IN_PROGRESS) {
            currentEdge.increaseUnfinished();
        }
    }
}
