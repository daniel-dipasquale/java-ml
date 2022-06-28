package com.dipasquale.search.mcts.simulation;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.seek.ComprehensiveSeekPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSimulationPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> implements SimulationPolicy<TAction, TEdge, TState, TSearchNode> {
    private final ComprehensiveSeekPolicy comprehensiveSeekPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy;

    protected abstract TContext createContext();

    protected abstract void visit(TContext context, TSearchNode searchNode);

    private boolean allowSimulation(final int simulations, final int initialDepth, final int nextDepth, final TState state) {
        return state.getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID && (!state.isActionIntentional() || comprehensiveSeekPolicy.allowSimulation(simulations, initialDepth, nextDepth, state.getParticipantId()));
    }

    protected TSearchNode select(final TContext context, final TSearchNode searchNode) {
        return searchNode;
    }

    protected abstract void cleanUp(TContext context);

    @Override
    public TSearchNode simulate(final int simulations, final TSearchNode selectedSearchNode) {
        TContext context = createContext();
        TSearchNode currentSearchNode = selectedSearchNode;
        TState currentState = currentSearchNode.getState();
        int initialDepth = currentSearchNode.getStateId().getDepth();
        int nextDepth = initialDepth + 1;

        try {
            if (allowSimulation(simulations, initialDepth, nextDepth, currentState)) {
                visit(context, currentSearchNode);

                do {
                    TSearchNode childSearchNode = traversalPolicy.next(simulations, currentSearchNode);

                    if (childSearchNode == null) {
                        return select(context, currentSearchNode);
                    }

                    currentSearchNode = childSearchNode;
                    currentState = currentSearchNode.getState();
                    nextDepth++;
                    visit(context, currentSearchNode);
                } while (allowSimulation(simulations, initialDepth, nextDepth, currentState));
            }

            return select(context, currentSearchNode);
        } finally {
            cleanUp(context);
        }
    }
}
