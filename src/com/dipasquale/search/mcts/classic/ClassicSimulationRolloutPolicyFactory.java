package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.MultiTraversalPolicy;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ClassicSimulationRolloutPolicyFactory<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> implements ObjectFactory<TraversalPolicy<TState, ClassicEdge, TEnvironment>> {
    private final ClassicChildrenInitializerTraversalPolicy<TState, TEnvironment> childrenInitializerTraversalPolicy;
    private final RandomSupport randomSupport;

    @Override
    public TraversalPolicy<TState, ClassicEdge, TEnvironment> create() {
        List<TraversalPolicy<TState, ClassicEdge, TEnvironment>> simulationRolloutPolicies = new ArrayList<>();

        simulationRolloutPolicies.add(childrenInitializerTraversalPolicy);
        simulationRolloutPolicies.add(new ClassicSimulationRolloutPolicy<>(randomSupport));

        return new MultiTraversalPolicy<>(simulationRolloutPolicies);
    }
}
