package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.IntentRegulatorTraversalPolicy;
import com.dipasquale.search.mcts.common.IntentionalTraversalPolicy;
import com.dipasquale.search.mcts.common.UnintentionalTraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroSelectionPolicyFactory<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements ObjectFactory<AlphaZeroSelectionPolicy<TAction, TState, TSearchNode>> {
    private final SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator;
    private final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel;
    private final RandomSupport randomSupport;
    private final ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> expansionPolicy;

    @Override
    public AlphaZeroSelectionPolicy<TAction, TState, TSearchNode> create() {
        IntentionalTraversalPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> intentionalTraversalPolicy = new IntentionalTraversalPolicy<>(selectionConfidenceCalculator);

        if (traversalModel.isEveryStateIntentional()) {
            return new AlphaZeroSelectionPolicy<>(intentionalTraversalPolicy, expansionPolicy);
        }

        UnintentionalTraversalPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> unintentionalTraversalPolicy = new UnintentionalTraversalPolicy<>(randomSupport);
        IntentRegulatorTraversalPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> intentRegulatorTraversalPolicy = new IntentRegulatorTraversalPolicy<>(intentionalTraversalPolicy, unintentionalTraversalPolicy);

        return new AlphaZeroSelectionPolicy<>(intentRegulatorTraversalPolicy, expansionPolicy);
    }
}
