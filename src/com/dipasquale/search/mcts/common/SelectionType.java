package com.dipasquale.search.mcts.common;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SelectionType {
    INTENTIONAL_ONLY,
    MIXED;

    public static <T extends Action> SelectionType determine(final ExplorationHeuristic<T> explorationHeuristic) {
        if (explorationHeuristic == null) {
            return INTENTIONAL_ONLY;
        }

        return MIXED;
    }

    public <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> TraversalPolicy<TAction, TEdge, TState, TSearchNode> createTraversalPolicy(final RandomSupport randomSupport, final TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy) {
        return switch (this) {
            case MIXED -> {
                UnintentionalTraversalPolicy<TAction, TEdge, TState, TSearchNode> unintentionalTraversalPolicy = new UnintentionalTraversalPolicy<>(randomSupport);

                yield new IntentRegulatorTraversalPolicy<>(intentionalTraversalPolicy, unintentionalTraversalPolicy);
            }

            case INTENTIONAL_ONLY -> intentionalTraversalPolicy;
        };
    }
}
