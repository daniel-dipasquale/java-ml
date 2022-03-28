package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.IntentRegulatorTraversalPolicy;
import com.dipasquale.search.mcts.common.IntentionalTraversalPolicy;
import com.dipasquale.search.mcts.common.UnintentionalTraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroSelectionPolicyFactory<TAction extends Action, TState extends State<TAction, TState>> implements ObjectFactory<AlphaZeroSelectionPolicy<TAction, TState>> {
    private final SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator;
    private final AlphaZeroModel<TAction, TState> traversalModel;
    private final ExpansionPolicy<TAction, AlphaZeroEdge, TState> expansionPolicy;

    @Override
    public AlphaZeroSelectionPolicy<TAction, TState> create() {
        IntentionalTraversalPolicy<TAction, AlphaZeroEdge, TState> intentionalTraversalPolicy = new IntentionalTraversalPolicy<>(selectionConfidenceCalculator);

        if (traversalModel.isEveryStateIntentional()) {
            return new AlphaZeroSelectionPolicy<>(intentionalTraversalPolicy, expansionPolicy);
        }

        UniformRandomSupport randomSupport = new UniformRandomSupport();
        UnintentionalTraversalPolicy<TAction, AlphaZeroEdge, TState> unintentionalTraversalPolicy = new UnintentionalTraversalPolicy<>(randomSupport);
        IntentRegulatorTraversalPolicy<TAction, AlphaZeroEdge, TState> intentRegulatorTraversalPolicy = new IntentRegulatorTraversalPolicy<>(intentionalTraversalPolicy, unintentionalTraversalPolicy);

        return new AlphaZeroSelectionPolicy<>(intentRegulatorTraversalPolicy, expansionPolicy);
    }
}
