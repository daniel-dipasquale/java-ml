package com.dipasquale.search.mcts.common;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CommonSimulationRolloutPolicyFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ObjectFactory<CommonSimulationRolloutPolicy<TAction, TEdge, TState>> {
    private final ExtendedSearchPolicy searchPolicy;
    private final RandomSupport randomSupport;
    private final SelectionType selectionType;
    private final ExpansionPolicy<TAction, TEdge, TState> expansionPolicy;

    @Override
    public CommonSimulationRolloutPolicy<TAction, TEdge, TState> create() {
        CommonSimulationRolloutTraversalPolicy<TAction, TEdge, TState> intentionalTraversalPolicy = new CommonSimulationRolloutTraversalPolicy<>(randomSupport);

        return new CommonSimulationRolloutPolicy<>(searchPolicy, selectionType.createTraversalPolicy(intentionalTraversalPolicy), expansionPolicy);
    }
}
