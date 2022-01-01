package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.MultiSelectionPolicy;
import com.dipasquale.search.mcts.core.SearchEdgeFactory;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ClassicSimulationRolloutPolicyFactory<T extends SearchState> implements ObjectFactory<SelectionPolicy<T, ClassicSearchEdge>> {
    private final ClassicSimulationRolloutType classicSimulationRolloutType;
    private final ClassicChildrenInitializerSelectionPolicy<T> classicChildrenInitializerSelectionPolicy;
    private final SearchEdgeFactory<ClassicSearchEdge> edgeFactory;
    private final RandomSupport randomSupport;

    @Override
    public SelectionPolicy<T, ClassicSearchEdge> create() {
        return switch (classicSimulationRolloutType) {
            case STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME -> {
                List<SelectionPolicy<T, ClassicSearchEdge>> simulationRolloutPolicies = new ArrayList<>();

                simulationRolloutPolicies.add(classicChildrenInitializerSelectionPolicy);
                simulationRolloutPolicies.add(new ClassicDeterministicSelectionPolicy<>(randomSupport));

                yield new MultiSelectionPolicy<>(simulationRolloutPolicies);
            }

            case ALL_STOCHASTIC -> new ClassicStochasticSelectionPolicy<>(edgeFactory, randomSupport);
        };
    }
}
