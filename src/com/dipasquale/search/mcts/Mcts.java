package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.iterator.FlatIterator;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public final class Mcts<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private final SearchPolicy searchPolicy;
    private final Provider<TAction, TEdge, TState> provider;
    private final SelectionPolicy<TAction, TEdge, TState> selectionPolicy;
    private final SimulationRolloutPolicy<TAction, TEdge, TState> simulationRolloutPolicy;
    private final BackPropagationPolicy<TAction, TEdge, TState, ?> backPropagationPolicy;
    private final ProposalStrategy<TAction, TEdge, TState> proposalStrategy;
    private final List<ResetHandler> resetHandlers;

    private TAction findBestAction(final TState state) {
        SearchNode<TAction, TEdge, TState> rootSearchNode = provider.recallOrCreate(state);
        int rootDepth = rootSearchNode.getState().getDepth();

        for (int simulations = 0, simulationsPlusOne = simulations + 1; simulationsPlusOne - simulations == 1 && searchPolicy.allowSelection(simulationsPlusOne, rootDepth); simulationsPlusOne++) {
            SearchNode<TAction, TEdge, TState> selectedSearchNode = selectionPolicy.select(simulationsPlusOne, rootSearchNode);

            if (selectedSearchNode != null) {
                SearchNode<TAction, TEdge, TState> leafSearchNode = simulationRolloutPolicy.simulate(simulations, selectedSearchNode);

                backPropagationPolicy.process(leafSearchNode);
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
