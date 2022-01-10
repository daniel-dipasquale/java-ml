package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.HighestConfidenceTraversalPolicy;
import com.dipasquale.search.mcts.core.MultiTraversalPolicy;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import com.dipasquale.search.mcts.core.UnexploredFirstTraversalPolicy;

import java.util.ArrayList;
import java.util.List;

final class ClassicSelectionPolicy<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> implements TraversalPolicy<TState, ClassicEdge, TEnvironment> {
    private final TraversalPolicy<TState, ClassicEdge, TEnvironment> finalTraversalPolicy;
    private final TraversalPolicy<TState, ClassicEdge, TEnvironment> partialTraversalPolicy;

    ClassicSelectionPolicy(final ClassicChildrenInitializerTraversalPolicy<TState, TEnvironment> childrenInitializerTraversalPolicy, final ConfidenceCalculator<ClassicEdge> confidenceCalculator) {
        this.finalTraversalPolicy = createFinalTraversalPolicy(childrenInitializerTraversalPolicy);
        this.partialTraversalPolicy = new HighestConfidenceTraversalPolicy<>(confidenceCalculator);
    }

    private static <TState extends State, TEnvironment extends Environment<TState, TEnvironment>> MultiTraversalPolicy<TState, ClassicEdge, TEnvironment> createFinalTraversalPolicy(final ClassicChildrenInitializerTraversalPolicy<TState, TEnvironment> childrenInitializerTraversalPolicy) {
        List<TraversalPolicy<TState, ClassicEdge, TEnvironment>> traversalPolicies = new ArrayList<>();

        traversalPolicies.add(childrenInitializerTraversalPolicy);
        traversalPolicies.add(UnexploredFirstTraversalPolicy.getInstance());

        return new MultiTraversalPolicy<>(traversalPolicies);
    }

    @Override
    public SearchNode<TState, ClassicEdge, TEnvironment> next(final int simulations, final SearchNode<TState, ClassicEdge, TEnvironment> node) {
        SearchNode<TState, ClassicEdge, TEnvironment> temporaryNode = finalTraversalPolicy.next(simulations, node);

        if (temporaryNode != null) {
            return temporaryNode;
        }

        SearchNode<TState, ClassicEdge, TEnvironment> nextNode = node;

        while (true) {
            nextNode = partialTraversalPolicy.next(simulations, nextNode);

            if (nextNode == null) {
                return null;
            }

            temporaryNode = finalTraversalPolicy.next(simulations, nextNode);

            if (temporaryNode != null) {
                return temporaryNode;
            }
        }
    }
}
