package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;

import java.util.ArrayList;
import java.util.List;

public final class CommonSelectionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SelectionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> desiredTraversalPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> undesiredTraversalPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    private static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> FirstFoundTraversalPolicy<TAction, TEdge, TState, TSearchNode> createWantedTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        List<TraversalPolicy<TAction, TEdge, TState, TSearchNode>> traversalPolicies = new ArrayList<>();

        traversalPolicies.add(new ExpansionTraversalPolicy<>(expansionPolicy));
        traversalPolicies.add(UnexploredPrimerTraversalPolicy.getInstance());

        return new FirstFoundTraversalPolicy<>(traversalPolicies);
    }

    CommonSelectionPolicy(final TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        this.desiredTraversalPolicy = createWantedTraversalPolicy(expansionPolicy);
        this.undesiredTraversalPolicy = traversalPolicy;
        this.expansionPolicy = expansionPolicy;
    }

    @Override
    public TSearchNode select(final int simulations, final TSearchNode rootSearchNode) {
        for (TSearchNode nextSearchNode = rootSearchNode, desiredSearchNode = desiredTraversalPolicy.next(simulations, nextSearchNode); true; desiredSearchNode = desiredTraversalPolicy.next(simulations, nextSearchNode)) {
            if (desiredSearchNode != null) {
                assert !desiredSearchNode.isExpanded();

                expansionPolicy.expand(desiredSearchNode);

                if (desiredSearchNode.getState().isIntentional()) {
                    return desiredSearchNode;
                }

                nextSearchNode = undesiredTraversalPolicy.next(simulations, desiredSearchNode);
            } else {
                nextSearchNode = undesiredTraversalPolicy.next(simulations, nextSearchNode);
            }

            if (nextSearchNode == null) {
                return null;
            }
        }
    }
}
