package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.BackPropagationObserver;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

public final class ClassicBackPropagationPolicy<TAction extends Action, TState extends State<TAction, TState>> extends AbstractBackPropagationPolicy<TAction, ClassicEdge, TState> {
    public ClassicBackPropagationPolicy(final BackPropagationObserver<TAction, ClassicEdge, TState> observer) {
        super(observer);
    }

    @Override
    protected void process(final SearchNode<TAction, ClassicEdge, TState> leafNode, final int simulationStatusId, final SearchNode<TAction, ClassicEdge, TState> currentNode) {
        ClassicEdge currentEdge = currentNode.getEdge();

        currentEdge.increaseVisited();

        if (currentNode.getAction().getParticipantId() == simulationStatusId) {
            currentEdge.increaseWon();
        } else if (simulationStatusId == MonteCarloTreeSearch.DRAWN) {
            currentEdge.increaseDrawn();
        } else if (simulationStatusId == MonteCarloTreeSearch.IN_PROGRESS) {
            currentEdge.increaseUnfinished();
        }
    }
}
