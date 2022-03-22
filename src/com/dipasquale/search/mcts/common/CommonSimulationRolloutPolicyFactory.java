package com.dipasquale.search.mcts.common;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class CommonSimulationRolloutPolicyFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ObjectFactory<CommonSimulationRolloutPolicy<TAction, TEdge, TState>> {
    private final ExtendedSearchPolicy searchPolicy;
    private final ExpanderTraversalPolicy<TAction, TEdge, TState> expanderTraversalPolicy;
    private final RandomSupport randomSupport;

    @Override
    public CommonSimulationRolloutPolicy<TAction, TEdge, TState> create() {
        List<TraversalPolicy<TAction, TEdge, TState>> simulationRolloutPolicies = new ArrayList<>();

        simulationRolloutPolicies.add(expanderTraversalPolicy);
        simulationRolloutPolicies.add(new CommonSimulationRolloutTraversalPolicy<>(randomSupport));

        return new CommonSimulationRolloutPolicy<>(searchPolicy, new FirstFoundTraversalPolicy<>(simulationRolloutPolicies));
    }
}
