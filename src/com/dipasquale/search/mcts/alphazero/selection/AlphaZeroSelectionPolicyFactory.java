package com.dipasquale.search.mcts.alphazero.selection;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.intention.IntentRegulatorTraversalPolicy;
import com.dipasquale.search.mcts.intention.UnintentionalTraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AlphaZeroSelectionPolicyFactory<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements ObjectFactory<AlphaZeroSelectionPolicy<TAction, TState, TSearchNode>> {
    private final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel;
    private final TraversalPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> intentionalTraversalPolicy;
    private final ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> expansionPolicy;
    private final RandomSupport randomSupport;

    @Override
    public AlphaZeroSelectionPolicy<TAction, TState, TSearchNode> create() {
        if (traversalModel.isEveryStateIntentional()) {
            return new AlphaZeroSelectionPolicy<>(intentionalTraversalPolicy, expansionPolicy);
        }

        UnintentionalTraversalPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> unintentionalTraversalPolicy = new UnintentionalTraversalPolicy<>(randomSupport);
        IntentRegulatorTraversalPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> intentRegulatorTraversalPolicy = new IntentRegulatorTraversalPolicy<>(intentionalTraversalPolicy, unintentionalTraversalPolicy);

        return new AlphaZeroSelectionPolicy<>(intentRegulatorTraversalPolicy, expansionPolicy);
    }
}
