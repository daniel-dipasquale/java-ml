package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.HighestConfidenceSelectionPolicy;
import com.dipasquale.search.mcts.core.MultiSelectionPolicy;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import com.dipasquale.search.mcts.core.UnexploredFirstSelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ClassicSelectionPolicyFactory<T extends SearchState> implements ObjectFactory<SelectionPolicy<T, ClassicSearchEdge>> {
    private final ClassicChildrenInitializerSelectionPolicy<T> childrenInitializerSelectionPolicy;
    private final ConfidenceCalculator<ClassicSearchEdge> confidenceCalculator;

    @Override
    public SelectionPolicy<T, ClassicSearchEdge> create() {
        List<SelectionPolicy<T, ClassicSearchEdge>> selectionPolicies = new ArrayList<>();

        selectionPolicies.add(childrenInitializerSelectionPolicy);
        selectionPolicies.add(new UnexploredFirstSelectionPolicy<>());
        selectionPolicies.add(new HighestConfidenceSelectionPolicy<>(confidenceCalculator));

        return new MultiSelectionPolicy<>(selectionPolicies);
    }
}
