package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroSelectionPolicy<TAction extends Action, TState extends State<TAction, TState>> implements SelectionPolicy<TAction, AlphaZeroEdge, TState> {
    private final AlphaZeroExpander<TAction, TState> expander;
    private final TraversalPolicy<TAction, AlphaZeroEdge, TState> traversalPolicy;

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> select(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> rootSearchNode) {
        for (SearchNode<TAction, AlphaZeroEdge, TState> nextSearchNode = rootSearchNode, temporarySearchNode = traversalPolicy.next(simulations, nextSearchNode); true; temporarySearchNode = traversalPolicy.next(simulations, nextSearchNode)) {
            if (temporarySearchNode == null) {
                if (nextSearchNode == rootSearchNode && nextSearchNode.getEdge().getVisited() > 0) {
                    return null;
                }

                if (!nextSearchNode.isExpanded()) {
                    expander.expand(nextSearchNode);
                }

                return nextSearchNode;
            }

            nextSearchNode = temporarySearchNode;
        }
    }
}
