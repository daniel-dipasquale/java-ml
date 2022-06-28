package com.dipasquale.search.mcts.seek;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNodeExplorer;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;

public final class StandardSeekStrategy<TAction, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSeekStrategy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    public StandardSeekStrategy(final SeekPolicy seekPolicy, final SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> simulationPolicy, final BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> backPropagationPolicy, final SearchNodeExplorer<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> searchNodeExplorer) {
        super(seekPolicy, selectionPolicy, simulationPolicy, backPropagationPolicy, searchNodeExplorer);
    }
}
