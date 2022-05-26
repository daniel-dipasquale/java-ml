package com.dipasquale.search.mcts.seek;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeManager;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationRolloutPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSeekStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SeekStrategy<TAction, TEdge, TState, TSearchNode> {
    private final SeekPolicy seekPolicy;
    private final SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy;
    private final SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> simulationRolloutPolicy;
    private final BackPropagationPolicy<TAction, TEdge, TState, TSearchNode> backPropagationPolicy;
    private final SearchNodeManager<TAction, TEdge, TState, TSearchNode> searchNodeManager;

    private TSearchNode simulate(final int simulations, final TSearchNode selectedSearchNode) {
        if (selectedSearchNode == null) {
            return null;
        }

        return simulationRolloutPolicy.simulate(simulations, selectedSearchNode);
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
                    continueSearching = !searchNodeManager.isFullyExplored(rootSearchNode);
                }
            }
        } finally {
            seekPolicy.end();
        }
    }
}
