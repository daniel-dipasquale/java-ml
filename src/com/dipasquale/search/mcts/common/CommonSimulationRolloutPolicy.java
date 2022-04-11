package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CommonSimulationRolloutPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> {
    private final ExtendedSearchPolicy searchPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    private boolean allowSimulationRollout(final int simulations, final int initialDepth, final int nextDepth, final TState state) {
        return searchPolicy.allowSimulationRollout(simulations, initialDepth, nextDepth, state.getParticipantId()) || !state.isIntentional();
    }

    @Override
    public TSearchNode simulate(final int simulations, final TSearchNode selectedSearchNode) {
        TSearchNode currentSearchNode = selectedSearchNode;
        TState currentState = currentSearchNode.getState();
        int initialDepth = currentState.getDepth();

        while (currentState.getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
            int nextDepth = currentState.getDepth() + 1;

            if (!allowSimulationRollout(simulations, initialDepth, nextDepth, currentState)) {
                return currentSearchNode;
            }

            TSearchNode childSearchNode = traversalPolicy.next(simulations, currentSearchNode);

            if (childSearchNode == null) {
                return currentSearchNode;
            }

            if (!childSearchNode.isExpanded()) {
                expansionPolicy.expand(childSearchNode);
            }

            currentSearchNode = childSearchNode;
            currentState = currentSearchNode.getState();
        }

        return currentSearchNode;
    }
}
