package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CommonSearchStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchStrategy<TAction, TEdge, TState, TSearchNode> {
    private final SearchPolicy searchPolicy;
    private final SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy;
    private final SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> simulationRolloutPolicy;
    private final BackPropagationPolicy<TAction, TEdge, TState, TSearchNode, ?> backPropagationPolicy;

    @Override
    public void process(final TSearchNode rootSearchNode) {
        searchPolicy.begin();

        try {
            boolean continueSearching = true;

            for (int i = 1, c = searchPolicy.getMaximumSelectionCount(); continueSearching && i <= c; i++) {
                TSearchNode selectedSearchNode = selectionPolicy.select(i, rootSearchNode);

                if (selectedSearchNode != null) {
                    TSearchNode leafSearchNode = simulationRolloutPolicy.simulate(i, selectedSearchNode);

                    backPropagationPolicy.process(leafSearchNode);
                } else {
                    continueSearching = false;
                }
            }
        } finally {
            searchPolicy.end();
        }
    }
}
