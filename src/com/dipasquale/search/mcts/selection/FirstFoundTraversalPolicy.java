package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class FirstFoundTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private final List<TraversalPolicy<TAction, TEdge, TState, TSearchNode>> traversalPolicies;

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        for (TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy : traversalPolicies) {
            TSearchNode nextSearchNode = traversalPolicy.next(simulations, searchNode);

            if (nextSearchNode != null) {
                return nextSearchNode;
            }
        }

        return null;
    }
}
