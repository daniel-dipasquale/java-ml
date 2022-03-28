package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AlphaZeroSimulationRolloutPolicy<TAction extends Action, TState extends State<TAction, TState>> implements SimulationRolloutPolicy<TAction, AlphaZeroEdge, TState> {
    private static final AlphaZeroSimulationRolloutPolicy<?, ?> INSTANCE = new AlphaZeroSimulationRolloutPolicy<>();

    public static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroSimulationRolloutPolicy<TAction, TState> getInstance() {
        return (AlphaZeroSimulationRolloutPolicy<TAction, TState>) INSTANCE;
    }

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> simulate(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> selectedSearchNode) {
        return selectedSearchNode;
    }
}
