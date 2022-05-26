package com.dipasquale.search.mcts.seek;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNodeManager;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationRolloutPolicy;

public final class StandardSeekStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSeekStrategy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    public StandardSeekStrategy(final SeekPolicy seekPolicy, final SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> backPropagationPolicy, final SearchNodeManager<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> searchNodeManager) {
        super(seekPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, searchNodeManager);
    }
}
