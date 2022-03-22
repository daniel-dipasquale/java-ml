package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.ExplorationProbabilityCalculator;
import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class NeuralNetworkAlphaZeroContext<TAction extends Action, TState extends State<TAction, TState>> {
    private final SearchNode<TAction, AlphaZeroEdge, TState> searchNode;
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final AlphaZeroPredictor<TAction, TState> predictor;
    private final ValueHeuristic<TAction, TState> valueHeuristic;
    private final ExplorationProbabilityCalculator<TAction> policyCalculator;
}
