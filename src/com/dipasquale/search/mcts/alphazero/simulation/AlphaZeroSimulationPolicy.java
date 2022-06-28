package com.dipasquale.search.mcts.alphazero.simulation;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlphaZeroSimulationPolicy<TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements SimulationPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> {
    private static final AlphaZeroSimulationPolicy<?, ?, ?> INSTANCE = new AlphaZeroSimulationPolicy<>();

    public static <TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> AlphaZeroSimulationPolicy<TAction, TState, TSearchNode> getInstance() {
        return (AlphaZeroSimulationPolicy<TAction, TState, TSearchNode>) INSTANCE;
    }

    @Override
    public TSearchNode simulate(final int simulations, final TSearchNode selectedSearchNode) {
        return selectedSearchNode;
    }
}
