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
final class AlphaZeroSelectionPolicy<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements SelectionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> {
    private final TraversalPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> traversalPolicy;
    private final ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> expansionPolicy;

    @Override
    public TSearchNode select(final int simulations, final TSearchNode rootSearchNode) {
        for (TSearchNode nextSearchNode = rootSearchNode, temporarySearchNode = traversalPolicy.next(simulations, nextSearchNode); true; temporarySearchNode = traversalPolicy.next(simulations, nextSearchNode)) {
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
