package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.iterator.FlatIterator;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public final class Mcts<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private final SearchPolicy searchPolicy;
    private final EdgeFactory<TEdge> edgeFactory;
    private final SearchNodeCache<TAction, TEdge, TState> nodeCache;
    private final SelectionPolicy<TAction, TEdge, TState> selectionPolicy;
    private final SimulationRolloutPolicy<TAction, TEdge, TState> simulationRolloutPolicy;
    private final BackPropagationPolicy<TAction, TEdge, TState, ?> backPropagationPolicy;
    private final SearchNodeProposalStrategy<TAction, TEdge, TState> nodeProposalStrategy;
    private final List<ResetHandler> resetHandlers;

    private SearchNode<TAction, TEdge, TState> getRootNode(final TState state) {
        if (nodeCache != null) {
            return nodeCache.retrieve(state);
        }

        return SearchNode.createRoot(edgeFactory, state);
    }

    private TAction findBestAction(final TState state) {
        if (state.getStatusId() != MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
            return null;
        }

        SearchNode<TAction, TEdge, TState> rootNode = getRootNode(state);
        int rootDepth = rootNode.getState().getDepth();

        for (int simulations = 0, simulationsPlusOne = simulations + 1; simulationsPlusOne - simulations == 1 && searchPolicy.allowSelection(simulationsPlusOne, rootDepth); simulationsPlusOne++) {
            SearchNode<TAction, TEdge, TState> selectedNode = selectionPolicy.select(simulationsPlusOne, rootNode);

            if (selectedNode != null) {
                if (simulationRolloutPolicy != null && selectedNode.getState().getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
                    SearchNode<TAction, TEdge, TState> leafNode = simulationRolloutPolicy.simulate(simulations, selectedNode);

                    backPropagationPolicy.process(leafNode);
                } else {
                    backPropagationPolicy.process(selectedNode);
                }

                simulations = simulationsPlusOne;
            }
        }

        int nextDepth = rootDepth + 1;
        List<Iterator<SearchNode<TAction, TEdge, TState>>> childNodeIterators = List.of(rootNode.getExplorableChildren().iterator(), rootNode.getFullyExploredChildren().iterator());
        Iterable<SearchNode<TAction, TEdge, TState>> childNodes = () -> FlatIterator.fromIterators(childNodeIterators);
        SearchNode<TAction, TEdge, TState> bestNode = nodeProposalStrategy.proposeBestNode(rootNode.getEdge().getVisited(), nextDepth, childNodes);

        if (bestNode == null) {
            return null;
        }

        return bestNode.getAction();
    }

    public TAction proposeNextAction(final TState state) {
        searchPolicy.begin();

        try {
            return findBestAction(state);
        } finally {
            searchPolicy.end();
        }
    }

    public void reset() {
        for (ResetHandler resetHandler : resetHandlers) {
            resetHandler.reset();
        }
    }
}
