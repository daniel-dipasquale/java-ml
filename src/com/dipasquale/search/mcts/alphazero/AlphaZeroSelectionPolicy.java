package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroSelectionPolicy<TAction extends Action, TState extends State<TAction, TState>> implements SelectionPolicy<TAction, AlphaZeroEdge, TState> {
    private final TraversalPolicy<TAction, AlphaZeroEdge, TState> traversalPolicy;
    private final ExpansionPolicy<TAction, AlphaZeroEdge, TState> expansionPolicy;

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> select(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> rootSearchNode) {
        for (SearchNode<TAction, AlphaZeroEdge, TState> nextSearchNode = rootSearchNode, temporarySearchNode = traversalPolicy.next(simulations, nextSearchNode); true; temporarySearchNode = traversalPolicy.next(simulations, nextSearchNode)) {
            if (temporarySearchNode == null) {
                if (nextSearchNode == rootSearchNode && nextSearchNode.getEdge().getVisited() > 0) {
                    return null;
                }

                assert !nextSearchNode.isExpanded();

                expansionPolicy.expand(nextSearchNode);

                if (nextSearchNode.getState().isIntentional()) {
                    return nextSearchNode;
                }
            }

            nextSearchNode = temporarySearchNode;
        }
    }
}
