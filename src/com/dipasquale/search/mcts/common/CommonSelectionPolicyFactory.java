package com.dipasquale.search.mcts.common;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CommonSelectionPolicyFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ObjectFactory<CommonSelectionPolicy<TAction, TEdge, TState>> {
    private final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator;
    private final SelectionType selectionType;
    private final ExpansionPolicy<TAction, TEdge, TState> expansionPolicy;

    @Override
    public CommonSelectionPolicy<TAction, TEdge, TState> create() {
        IntentionalTraversalPolicy<TAction, TEdge, TState> intentionalTraversalPolicy = new IntentionalTraversalPolicy<>(selectionConfidenceCalculator);

        return new CommonSelectionPolicy<>(selectionType.createTraversalPolicy(intentionalTraversalPolicy), expansionPolicy);
    }
}
