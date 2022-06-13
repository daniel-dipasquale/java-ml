package com.dipasquale.search.mcts.seek;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeExplorer;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSeekStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SeekStrategy<TAction, TEdge, TState, TSearchNode> {
    private final SeekPolicy seekPolicy;
    private final SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy;
    private final SimulationPolicy<TAction, TEdge, TState, TSearchNode> simulationPolicy;
    private final BackPropagationPolicy<TAction, TEdge, TState, TSearchNode> backPropagationPolicy;
    private final SearchNodeExplorer<TAction, TEdge, TState, TSearchNode> searchNodeExplorer;

    private TSearchNode simulate(final int simulations, final TSearchNode selectedSearchNode) {
        if (selectedSearchNode == null) {
            return null;
        }

        return simulationPolicy.simulate(simulations, selectedSearchNode);
    }

    @Override
    public void process(final TSearchNode rootSearchNode) {
        seekPolicy.begin();

        try {
            boolean continueSearching = true;

            for (int i = 1, c = seekPolicy.getMaximumSelectionCount(); continueSearching && i <= c; i++) {
                TSearchNode selectedSearchNode = selectionPolicy.select(i, rootSearchNode);
                TSearchNode leafSearchNode = simulate(i, selectedSearchNode);

                if (leafSearchNode != null) {
                    backPropagationPolicy.process(rootSearchNode, selectedSearchNode, leafSearchNode);
                } else {
                    continueSearching = !searchNodeExplorer.isFullyExplored(rootSearchNode);
                }
            }
        } finally {
            seekPolicy.end();
        }
    }
}
