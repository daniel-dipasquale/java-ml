package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.FirstNonNullTraversalPolicy;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ClassicSimulationRolloutPolicy<TAction extends Action, TState extends State<TAction, TState>> implements SimulationRolloutPolicy<TAction, ClassicEdge, TState> {
    private final ClassicSearchPolicy searchPolicy;
    private final TraversalPolicy<TAction, ClassicEdge, TState> traversalPolicy;

    static <TAction extends Action, TState extends State<TAction, TState>> ClassicSimulationRolloutPolicy<TAction, TState> create(final ClassicSearchPolicy searchPolicy, final ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy, final RandomSupport randomSupport) {
        List<TraversalPolicy<TAction, ClassicEdge, TState>> simulationRolloutPolicies = new ArrayList<>();

        simulationRolloutPolicies.add(childrenInitializerTraversalPolicy);
        simulationRolloutPolicies.add(new ClassicSimulationRolloutTraversalPolicy<>(randomSupport));

        return new ClassicSimulationRolloutPolicy<>(searchPolicy, new FirstNonNullTraversalPolicy<>(simulationRolloutPolicies));

    }

    @Override
    public SearchNode<TAction, ClassicEdge, TState> simulate(final int simulations, final SearchNode<TAction, ClassicEdge, TState> selectedNode) {
        int initialDepth = selectedNode.getState().getDepth();

        for (SearchNode<TAction, ClassicEdge, TState> currentNode = selectedNode; true; ) {
            int nextDepth = currentNode.getState().getDepth() + 1;

            if (!searchPolicy.allowSimulationRollout(simulations, initialDepth, nextDepth)) {
                return currentNode;
            }

            SearchNode<TAction, ClassicEdge, TState> childNode = traversalPolicy.next(simulations, currentNode);

            if (childNode == null) {
                return currentNode;
            }

            currentNode = childNode;

            if (currentNode.getState().getStatusId() != MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
                return currentNode;
            }
        }
    }

    public SelectionPolicy<TAction, ClassicEdge, TState> createSelection() {
        return this::simulate;
    }
}
