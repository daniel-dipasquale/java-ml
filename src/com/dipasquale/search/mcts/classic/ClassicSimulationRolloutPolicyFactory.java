package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.FirstNonNullTraversalPolicy;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ClassicSimulationRolloutPolicyFactory<TAction extends Action, TState extends State<TAction, TState>> implements ObjectFactory<TraversalPolicy<TAction, ClassicEdge, TState>> {
    private final ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy;
    private final RandomSupport randomSupport;

    @Override
    public TraversalPolicy<TAction, ClassicEdge, TState> create() {
        List<TraversalPolicy<TAction, ClassicEdge, TState>> simulationRolloutPolicies = new ArrayList<>();

        simulationRolloutPolicies.add(childrenInitializerTraversalPolicy);
        simulationRolloutPolicies.add(new ClassicSimulationRolloutPolicy<>(randomSupport));

        return new FirstNonNullTraversalPolicy<>(simulationRolloutPolicies);
    }
}
