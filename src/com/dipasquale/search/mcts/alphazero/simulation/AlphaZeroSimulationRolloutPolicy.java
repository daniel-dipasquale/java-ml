package com.dipasquale.search.mcts.alphazero.simulation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.simulation.SimulationRolloutPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlphaZeroSimulationRolloutPolicy<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements SimulationRolloutPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> {
    private static final AlphaZeroSimulationRolloutPolicy<?, ?, ?> INSTANCE = new AlphaZeroSimulationRolloutPolicy<>();

    public static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> AlphaZeroSimulationRolloutPolicy<TAction, TState, TSearchNode> getInstance() {
        return (AlphaZeroSimulationRolloutPolicy<TAction, TState, TSearchNode>) INSTANCE;
    }

    @Override
    public TSearchNode simulate(final int simulations, final TSearchNode selectedSearchNode) {
        return selectedSearchNode;
    }
}
