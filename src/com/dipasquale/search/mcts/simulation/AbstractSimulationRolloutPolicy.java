package com.dipasquale.search.mcts.simulation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.seek.FullSeekPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSimulationRolloutPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> implements SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> {
    private final FullSeekPolicy searchPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    protected abstract TContext createContext();

    protected abstract void visit(TContext context, TSearchNode currentSearchNode);

    protected abstract boolean selected(TContext context, TSearchNode currentSearchNode);

    protected abstract void exit(TContext context);

    private boolean allowSimulationRollout(final int simulations, final int initialDepth, final int nextDepth, final TState state) {
        return searchPolicy.allowSimulationRollout(simulations, initialDepth, nextDepth, state.getParticipantId()) || !state.isIntentional();
    }

    private TSearchNode selectLeafIfAllowed(final TContext context, final TSearchNode currentSearchNode) {
        if (!selected(context, currentSearchNode)) {
            return null; // TODO: keep track of how many times this happens
        }

        return currentSearchNode;
    }

    @Override
    public TSearchNode simulate(final int simulations, final TSearchNode selectedSearchNode) {
        TContext context = createContext();

        try {
            TSearchNode currentSearchNode = selectedSearchNode;
            TState currentState = currentSearchNode.getState();
            int initialDepth = currentState.getDepth();

            for (boolean isFirstVisit = true; currentState.getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID; ) {
                int nextDepth = currentState.getDepth() + 1;

                if (!allowSimulationRollout(simulations, initialDepth, nextDepth, currentState)) {
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
