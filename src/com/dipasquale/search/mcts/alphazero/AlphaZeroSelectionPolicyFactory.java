package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.HighestConfidenceSelectionPolicy;
import com.dipasquale.search.mcts.core.MultiSelectionPolicy;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroSelectionPolicyFactory<T extends SearchState> implements ObjectFactory<SelectionPolicy<T, AlphaZeroSearchEdge>> {
    private final AlphaZeroChildrenInitializerSelectionPolicy<T> childrenInitializerSelectionPolicy;
    private final ConfidenceCalculator<AlphaZeroSearchEdge> confidenceCalculator;

    @Override
    public SelectionPolicy<T, AlphaZeroSearchEdge> create() {
        List<SelectionPolicy<T, AlphaZeroSearchEdge>> selectionPolicies = new ArrayList<>();

        selectionPolicies.add(childrenInitializerSelectionPolicy);
        selectionPolicies.add(new HighestConfidenceSelectionPolicy<>(confidenceCalculator));

        return new MultiSelectionPolicy<>(selectionPolicies);
    }
}
