package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.RewardHeuristic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class NeuralNetworkAlphaZeroContext<TAction extends Action, TState extends State<TAction, TState>> {
    private final SearchNode<TAction, AlphaZeroEdge, TState> searchNode;
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final AlphaZeroPredictor<TAction, TState> predictor;
    private final RewardHeuristic<TAction, TState> rewardHeuristic;
    private final ExplorationHeuristic<TAction> explorationHeuristic;
}
