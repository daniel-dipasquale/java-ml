package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.LeafNodeObserver;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

public final class ClassicBackPropagationPolicy<TAction extends Action, TState extends State<TAction, TState>> extends AbstractBackPropagationPolicy<TAction, ClassicEdge, TState> {
    public ClassicBackPropagationPolicy(final LeafNodeObserver<TAction, ClassicEdge, TState> leafNodeObserver) {
        super(leafNodeObserver);
    }

    @Override
    protected void process(final SearchNode<TAction, ClassicEdge, TState> leafNode, final int simulationStatusId, final SearchNode<TAction, ClassicEdge, TState> currentNode) {
        ClassicEdge currentEdge = currentNode.getEdge();

        currentEdge.increaseVisited();

        if (currentNode.getAction().getParticipantId() == simulationStatusId) {
            currentEdge.increaseWon();
        } else if (simulationStatusId == MonteCarloTreeSearch.DRAWN_STATUS_ID) {
            currentEdge.increaseDrawn();
        } else if (simulationStatusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
            currentEdge.increaseUnfinished();
        }
    }
}
