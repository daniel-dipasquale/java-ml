package com.dipasquale.search.mcts.alphazero.selection;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroSelectionPolicy<TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements SelectionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> {
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

                if (nextSearchNode.getState().isActionIntentional()) {
                    return nextSearchNode;
                }
            }

            nextSearchNode = temporarySearchNode;
        }
    }
}
