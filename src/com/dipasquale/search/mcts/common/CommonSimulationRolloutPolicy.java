package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CommonSimulationRolloutPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SimulationRolloutPolicy<TAction, TEdge, TState> {
    private final ExtendedSearchPolicy searchPolicy;
    private final TraversalPolicy<TAction, TEdge, TState> traversalPolicy;

    @Override
    public SearchNode<TAction, TEdge, TState> simulate(final int simulations, final SearchNode<TAction, TEdge, TState> selectedSearchNode) {
        int initialDepth = selectedSearchNode.getState().getDepth();

        for (SearchNode<TAction, TEdge, TState> currentSearchNode = selectedSearchNode; true; ) {
            int nextDepth = currentSearchNode.getState().getDepth() + 1;

            if (!searchPolicy.allowSimulationRollout(simulations, initialDepth, nextDepth)) {
                return currentSearchNode;
            }

            SearchNode<TAction, TEdge, TState> childSearchNode = traversalPolicy.next(simulations, currentSearchNode);

            if (childSearchNode == null) {
                return currentSearchNode;
            }

            currentSearchNode = childSearchNode;

            if (currentSearchNode.getState().getStatusId() != MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
                return currentSearchNode;
            }
        }
    }
}
