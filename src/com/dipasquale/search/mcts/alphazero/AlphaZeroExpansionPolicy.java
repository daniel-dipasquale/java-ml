package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroExpansionPolicy<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> {
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel;

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> void initializeProbableReward(final TSearchNode searchNode, final AlphaZeroPrediction<TAction, TState, TSearchNode> prediction) {
        searchNode.getEdge().setProbableReward(prediction.getValue());
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> void initializeExplorationProbabilities(final AlphaZeroPrediction<TAction, TState, TSearchNode> prediction) {
        List<TSearchNode> explorableChildren = prediction.getExplorableChildren();
        float[] policies = prediction.getPolicies();

        for (int i = 0, c = explorableChildren.size(); i < c; i++) {
            explorableChildren.get(i).getEdge().setExplorationProbability(policies[i]);
        }
    }

    @Override
    public void expand(final TSearchNode searchNode) {
        AlphaZeroPrediction<TAction, TState, TSearchNode> prediction = traversalModel.predict(searchNode, edgeFactory);

        initializeProbableReward(searchNode, prediction);
        initializeExplorationProbabilities(prediction);
        searchNode.setUnexploredChildren(List.of());
        searchNode.setExplorableChildren(prediction.getExplorableChildren());
        searchNode.setFullyExploredChildren(new ArrayList<>());
    }
}
