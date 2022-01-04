package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.HighestConfidenceSelectionPolicy;
import com.dipasquale.search.mcts.core.MultiSelectionPolicy;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroSelectionPolicyFactory<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> implements ObjectFactory<SelectionPolicy<TState, AlphaZeroSearchEdge, TEnvironment>> {
    private final AlphaZeroChildrenInitializerSelectionPolicy<TState, TEnvironment> childrenInitializerSelectionPolicy;
    private final ConfidenceCalculator<AlphaZeroSearchEdge> confidenceCalculator;

    @Override
    public SelectionPolicy<TState, AlphaZeroSearchEdge, TEnvironment> create() {
        List<SelectionPolicy<TState, AlphaZeroSearchEdge, TEnvironment>> selectionPolicies = new ArrayList<>();

        selectionPolicies.add(childrenInitializerSelectionPolicy);
        selectionPolicies.add(new HighestConfidenceSelectionPolicy<>(confidenceCalculator));

        return new MultiSelectionPolicy<>(selectionPolicies);
    }
}
