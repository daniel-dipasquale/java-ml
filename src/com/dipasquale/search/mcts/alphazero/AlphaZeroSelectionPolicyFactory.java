package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.HighestConfidenceTraversalPolicy;
import com.dipasquale.search.mcts.core.MultiTraversalPolicy;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroSelectionPolicyFactory<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> implements ObjectFactory<TraversalPolicy<TState, AlphaZeroEdge, TEnvironment>> {
    private final AlphaZeroChildrenInitializerTraversalPolicy<TState, TEnvironment> childrenInitializerTraversalPolicy;
    private final ConfidenceCalculator<AlphaZeroEdge> confidenceCalculator;

    @Override
    public TraversalPolicy<TState, AlphaZeroEdge, TEnvironment> create() {
        List<TraversalPolicy<TState, AlphaZeroEdge, TEnvironment>> selectionPolicies = new ArrayList<>();

        selectionPolicies.add(childrenInitializerTraversalPolicy);
        selectionPolicies.add(new HighestConfidenceTraversalPolicy<>(confidenceCalculator));

        return new MultiTraversalPolicy<>(selectionPolicies);
    }
}
