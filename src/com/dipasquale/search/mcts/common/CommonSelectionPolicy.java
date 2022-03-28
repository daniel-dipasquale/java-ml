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

public final class CommonSelectionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SelectionPolicy<TAction, TEdge, TState> {
    private final TraversalPolicy<TAction, TEdge, TState> desiredTraversalPolicy;
    private final TraversalPolicy<TAction, TEdge, TState> undesiredTraversalPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState> expansionPolicy;

    private static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> FirstFoundTraversalPolicy<TAction, TEdge, TState> createWantedTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState> expansionPolicy) {
        List<TraversalPolicy<TAction, TEdge, TState>> traversalPolicies = new ArrayList<>();

        traversalPolicies.add(new ExpansionTraversalPolicy<>(expansionPolicy));
        traversalPolicies.add(UnexploredPrimerTraversalPolicy.getInstance());

        return new FirstFoundTraversalPolicy<>(traversalPolicies);
    }

    CommonSelectionPolicy(final TraversalPolicy<TAction, TEdge, TState> traversalPolicy, final ExpansionPolicy<TAction, TEdge, TState> expansionPolicy) {
        this.desiredTraversalPolicy = createWantedTraversalPolicy(expansionPolicy);
        this.undesiredTraversalPolicy = traversalPolicy;
        this.expansionPolicy = expansionPolicy;
    }

    @Override
    public SearchNode<TAction, TEdge, TState> select(final int simulations, final SearchNode<TAction, TEdge, TState> rootSearchNode) {
        for (SearchNode<TAction, TEdge, TState> nextSearchNode = rootSearchNode, desiredSearchNode = desiredTraversalPolicy.next(simulations, nextSearchNode); true; desiredSearchNode = desiredTraversalPolicy.next(simulations, nextSearchNode)) {
            if (desiredSearchNode != null) {
                assert !desiredSearchNode.isExpanded();

                expansionPolicy.expand(desiredSearchNode);

                return desiredSearchNode;
            }

            nextSearchNode = undesiredTraversalPolicy.next(simulations, nextSearchNode);

            if (nextSearchNode == null) {
                return null;
            }
        }
    }
}
