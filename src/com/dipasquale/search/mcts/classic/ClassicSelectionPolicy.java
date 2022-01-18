package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.HighestConfidenceTraversalPolicy;
import com.dipasquale.search.mcts.core.MultiTraversalPolicy;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import com.dipasquale.search.mcts.core.UnexploredFirstTraversalPolicy;

import java.util.ArrayList;
import java.util.List;

final class ClassicSelectionPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, ClassicEdge, TState> {
    private final TraversalPolicy<TAction, ClassicEdge, TState> finalTraversalPolicy;
    private final TraversalPolicy<TAction, ClassicEdge, TState> partialTraversalPolicy;

    ClassicSelectionPolicy(final ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy, final ConfidenceCalculator<ClassicEdge> confidenceCalculator) {
        this.finalTraversalPolicy = createFinalTraversalPolicy(childrenInitializerTraversalPolicy);
        this.partialTraversalPolicy = new HighestConfidenceTraversalPolicy<>(confidenceCalculator);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> MultiTraversalPolicy<TAction, ClassicEdge, TState> createFinalTraversalPolicy(final ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy) {
        List<TraversalPolicy<TAction, ClassicEdge, TState>> traversalPolicies = new ArrayList<>();

        traversalPolicies.add(childrenInitializerTraversalPolicy);
        traversalPolicies.add(UnexploredFirstTraversalPolicy.getInstance());

        return new MultiTraversalPolicy<>(traversalPolicies);
    }

    @Override
    public SearchNode<TAction, ClassicEdge, TState> next(final int simulations, final SearchNode<TAction, ClassicEdge, TState> node) {
        for (SearchNode<TAction, ClassicEdge, TState> nextNode = node, temporaryNode = finalTraversalPolicy.next(simulations, nextNode); true; temporaryNode = finalTraversalPolicy.next(simulations, nextNode)) {
            if (temporaryNode != null) {
                return temporaryNode;
            }

            nextNode = partialTraversalPolicy.next(simulations, nextNode);

            if (nextNode == null) {
                return null;
            }
        }
    }
}
