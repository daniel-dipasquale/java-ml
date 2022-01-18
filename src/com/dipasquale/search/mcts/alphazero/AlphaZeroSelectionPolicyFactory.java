package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.HighestConfidenceTraversalPolicy;
import com.dipasquale.search.mcts.core.MultiTraversalPolicy;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroSelectionPolicyFactory<TAction extends Action, TState extends State<TAction, TState>> implements ObjectFactory<TraversalPolicy<TAction, AlphaZeroEdge, TState>> {
    private final AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy;
    private final ConfidenceCalculator<AlphaZeroEdge> confidenceCalculator;

    @Override
    public TraversalPolicy<TAction, AlphaZeroEdge, TState> create() {
        List<TraversalPolicy<TAction, AlphaZeroEdge, TState>> selectionPolicies = new ArrayList<>();

        selectionPolicies.add(childrenInitializerTraversalPolicy);
        selectionPolicies.add(new HighestConfidenceTraversalPolicy<>(confidenceCalculator));

        return new MultiTraversalPolicy<>(selectionPolicies);
    }
}
