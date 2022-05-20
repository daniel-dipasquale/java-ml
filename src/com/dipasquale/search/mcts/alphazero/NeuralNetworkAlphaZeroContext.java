package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.RewardHeuristic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class NeuralNetworkAlphaZeroContext<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> {
    private final TSearchNode searchNode;
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final SearchNodeGroupProvider<TAction, AlphaZeroEdge, TState, TSearchNode> searchNodeGroupProvider;
    private final AlphaZeroPredictor<TAction, TState, TSearchNode> predictor;
    private final RewardHeuristic<TAction, TState> rewardHeuristic;
    private final ExplorationHeuristic<TAction> explorationHeuristic;
}
