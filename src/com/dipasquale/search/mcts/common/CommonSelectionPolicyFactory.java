package com.dipasquale.search.mcts.common;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CommonSelectionPolicyFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ObjectFactory<CommonSelectionPolicy<TAction, TEdge, TState>> {
    private final ExpanderTraversalPolicy<TAction, TEdge, TState> expanderTraversalPolicy;
    private final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator;
    private final SelectionType type;

    @Override
    public CommonSelectionPolicy<TAction, TEdge, TState> create() {
        IntentionalTraversalPolicy<TAction, TEdge, TState> intentionalTraversalPolicy = new IntentionalTraversalPolicy<>(selectionConfidenceCalculator);

        TraversalPolicy<TAction, TEdge, TState> traversalPolicy = switch (type) {
            case MIXED -> {
                UniformRandomSupport randomSupport = new UniformRandomSupport();
                UnintentionalTraversalPolicy<TAction, TEdge, TState> unintentionalTraversalPolicy = new UnintentionalTraversalPolicy<>(randomSupport);

                yield new IntentRegulatorTraversalPolicy<>(intentionalTraversalPolicy, unintentionalTraversalPolicy);
            }

            case INTENTIONAL_ONLY -> intentionalTraversalPolicy;
        };

        return new CommonSelectionPolicy<>(expanderTraversalPolicy, traversalPolicy);
    }
}
