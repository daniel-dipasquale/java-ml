package com.dipasquale.search.mcts.intention;

import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.initialization.InitializationContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum IntentionType {
    INTENTIONAL_ONLY,
    MIXED;

    public static <T> IntentionType determine(final ExplorationHeuristic<T> explorationHeuristic) {
        if (explorationHeuristic != null) {
            return MIXED;
        }

        return INTENTIONAL_ONLY;
    }

    public <TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> TraversalPolicy<TAction, TEdge, TState, TSearchNode> createTraversalPolicy(final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext, final TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        return switch (this) {
            case MIXED -> {
                RandomSupport randomSupport = initializationContext.createRandomSupport();
                UnintentionalTraversalPolicy<TAction, TEdge, TState, TSearchNode> unintentionalTraversalPolicy = new UnintentionalTraversalPolicy<>(randomSupport, expansionPolicy);

                yield new IntentRegulatorTraversalPolicy<>(intentionalTraversalPolicy, unintentionalTraversalPolicy);
            }

            case INTENTIONAL_ONLY -> intentionalTraversalPolicy;
        };
    }
}
