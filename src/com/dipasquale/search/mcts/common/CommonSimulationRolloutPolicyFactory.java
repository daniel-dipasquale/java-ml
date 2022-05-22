package com.dipasquale.search.mcts.common;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CommonSimulationRolloutPolicyFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements ObjectFactory<CommonSimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode>> {
    private final FullSearchPolicy searchPolicy;
    private final SelectionType selectionType;
    private final RandomSupport randomSupport;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    @Override
    public CommonSimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> create() {
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy = selectionType.createTraversalPolicy(randomSupport, intentionalTraversalPolicy);

        return new CommonSimulationRolloutPolicy<>(searchPolicy, traversalPolicy, expansionPolicy);
    }
}
