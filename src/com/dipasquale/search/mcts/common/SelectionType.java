package com.dipasquale.search.mcts.common;

import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SelectionType {
    INTENTIONAL_ONLY,
    MIXED;

    public static <T extends Action> SelectionType determine(final ExplorationProbabilityCalculator<T> explorationProbabilityCalculator) {
        if (explorationProbabilityCalculator == null) {
            return INTENTIONAL_ONLY;
        }

        return MIXED;
    }

    public <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> TraversalPolicy<TAction, TEdge, TState> createTraversalPolicy(final TraversalPolicy<TAction, TEdge, TState> intentionalTraversalPolicy) {
        return switch (this) {
            case MIXED -> {
                UniformRandomSupport randomSupport = new UniformRandomSupport();
                UnintentionalTraversalPolicy<TAction, TEdge, TState> unintentionalTraversalPolicy = new UnintentionalTraversalPolicy<>(randomSupport);

                yield new IntentRegulatorTraversalPolicy<>(intentionalTraversalPolicy, unintentionalTraversalPolicy);
            }

            case INTENTIONAL_ONLY -> intentionalTraversalPolicy;
        };
    }
}
