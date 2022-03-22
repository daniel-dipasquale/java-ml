package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.iterator.FlatIterator;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public final class Mcts<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private final SearchPolicy searchPolicy;
    private final EdgeFactory<TEdge> edgeFactory;
    private final Cache<TAction, TEdge, TState> cache;
    private final SelectionPolicy<TAction, TEdge, TState> selectionPolicy;
    private final SimulationRolloutPolicy<TAction, TEdge, TState> simulationRolloutPolicy;
    private final BackPropagationPolicy<TAction, TEdge, TState, ?> backPropagationPolicy;
    private final ProposalStrategy<TAction, TEdge, TState> proposalStrategy;
    private final List<ResetHandler> resetHandlers;

    private SearchNode<TAction, TEdge, TState> getRootSearchNode(final TState state) {
        if (cache != null) {
            return cache.retrieve(state);
        }

        return SearchNode.createRoot(edgeFactory, state);
    }

    private TAction findBestAction(final TState state) {
        SearchNode<TAction, TEdge, TState> rootSearchNode = getRootSearchNode(state);
        int rootDepth = rootSearchNode.getState().getDepth();

        for (int simulations = 0, simulationsPlusOne = simulations + 1; simulationsPlusOne - simulations == 1 && searchPolicy.allowSelection(simulationsPlusOne, rootDepth); simulationsPlusOne++) {
            SearchNode<TAction, TEdge, TState> selectedSearchNode = selectionPolicy.select(simulationsPlusOne, rootSearchNode);

            if (selectedSearchNode != null) {
                if (simulationRolloutPolicy != null && selectedSearchNode.getState().getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
                    SearchNode<TAction, TEdge, TState> leafSearchNode = simulationRolloutPolicy.simulate(simulations, selectedSearchNode);

                    backPropagationPolicy.process(leafSearchNode);
                } else {
                    backPropagationPolicy.process(selectedSearchNode);
                }

                simulations = simulationsPlusOne;
            }
        }

        int nextDepth = rootDepth + 1;
        List<Iterator<SearchNode<TAction, TEdge, TState>>> childSearchNodeIterators = List.of(rootSearchNode.getExplorableChildren().iterator(), rootSearchNode.getFullyExploredChildren().iterator());
        Iterable<SearchNode<TAction, TEdge, TState>> childSearchNodes = () -> FlatIterator.fromIterators(childSearchNodeIterators);
        SearchNode<TAction, TEdge, TState> bestSearchNode = proposalStrategy.proposeBestNode(rootSearchNode.getEdge().getVisited(), nextDepth, childSearchNodes);

        if (bestSearchNode != null) {
            return bestSearchNode.getAction();
        }

        throw new UnableToProposeNextActionException();
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
