package com.dipasquale.search.mcts.simulation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.seek.ComprehensiveSeekPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSimulationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> implements SimulationPolicy<TAction, TEdge, TState, TSearchNode> {
    private final ComprehensiveSeekPolicy comprehensiveSeekPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    protected abstract TContext createContext();

    protected abstract void visit(TContext context, TSearchNode searchNode);

    private boolean allowSimulation(final int simulations, final int initialDepth, final int nextDepth, final TState state) {
        return comprehensiveSeekPolicy.allowSimulation(simulations, initialDepth, nextDepth, state.getParticipantId()) || !state.isIntentional();
    }

    protected abstract boolean select(TContext context, TSearchNode searchNode);

    protected abstract void exit(TContext context);

    private TSearchNode selectLeafIfAllowed(final TContext context, final TSearchNode searchNode) {
        if (!select(context, searchNode)) {
            return null; // TODO: keep track of how many times this happens
        }

        return searchNode;
    }

    @Override
    public TSearchNode simulate(final int simulations, final TSearchNode selectedSearchNode) {
        TContext context = createContext();

        try {
            TSearchNode currentSearchNode = selectedSearchNode;
            TState currentState = currentSearchNode.getState();
            int initialDepth = currentSearchNode.getStateId().getDepth();
            int nextDepth = initialDepth + 1;

            for (boolean isFirstVisit = true; currentState.getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID; ) {
                if (!allowSimulation(simulations, initialDepth, nextDepth, currentState)) {
                    return selectLeafIfAllowed(context, currentSearchNode);
                }

                if (isFirstVisit) {
                    visit(context, currentSearchNode);
                    isFirstVisit = false;
                }

                TSearchNode childSearchNode = traversalPolicy.next(simulations, currentSearchNode);

                if (childSearchNode == null) {
                    return selectLeafIfAllowed(context, currentSearchNode);
                }

                currentSearchNode = childSearchNode;
                currentState = currentSearchNode.getState();
                nextDepth++;
                visit(context, currentSearchNode);

                if (!currentSearchNode.isExpanded()) {
                    expansionPolicy.expand(currentSearchNode);
                }
            }

            return selectLeafIfAllowed(context, currentSearchNode);
        } finally {
            exit(context);
        }
    }
}
