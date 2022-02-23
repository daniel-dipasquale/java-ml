package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.FirstNonNullTraversalPolicy;
import com.dipasquale.search.mcts.core.HighestConfidenceTraversalPolicy;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import com.dipasquale.search.mcts.core.UnexploredFirstTraversalPolicy;

import java.util.ArrayList;
import java.util.List;

public final class ClassicSelectionPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, ClassicEdge, TState> {
    private final TraversalPolicy<TAction, ClassicEdge, TState> wantedTraversalPolicy;
    private final TraversalPolicy<TAction, ClassicEdge, TState> unwantedTraversalPolicy;

    private static <TAction extends Action, TState extends State<TAction, TState>> FirstNonNullTraversalPolicy<TAction, ClassicEdge, TState> createWantedTraversalPolicy(final ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy) {
        List<TraversalPolicy<TAction, ClassicEdge, TState>> traversalPolicies = new ArrayList<>();

        traversalPolicies.add(childrenInitializerTraversalPolicy);
        traversalPolicies.add(UnexploredFirstTraversalPolicy.getInstance());

        return new FirstNonNullTraversalPolicy<>(traversalPolicies);
    }

    public ClassicSelectionPolicy(final ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy, final ConfidenceCalculator<ClassicEdge> confidenceCalculator) {
        this.wantedTraversalPolicy = createWantedTraversalPolicy(childrenInitializerTraversalPolicy);
        this.unwantedTraversalPolicy = new HighestConfidenceTraversalPolicy<>(confidenceCalculator);
    }

    @Override
    public SearchNode<TAction, ClassicEdge, TState> next(final int simulations, final SearchNode<TAction, ClassicEdge, TState> node) {
        for (SearchNode<TAction, ClassicEdge, TState> nextNode = node, wantedNode = wantedTraversalPolicy.next(simulations, nextNode); true; wantedNode = wantedTraversalPolicy.next(simulations, nextNode)) {
            if (wantedNode != null) {
                return wantedNode;
            }

            nextNode = unwantedTraversalPolicy.next(simulations, nextNode);

            if (nextNode == null) {
                return null;
            }
        }
    }
}
