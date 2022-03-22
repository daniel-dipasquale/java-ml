package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.Expander;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroExpander<TAction extends Action, TState extends State<TAction, TState>> implements Expander<TAction, AlphaZeroEdge, TState> {
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final AlphaZeroModel<TAction, TState> traversalModel;
    private final Expander<TAction, AlphaZeroEdge, TState> additionalExpander;

    private static <TAction extends Action, TState extends State<TAction, TState>> void initializeProbableReward(final SearchNode<TAction, AlphaZeroEdge, TState> searchNode, final AlphaZeroPrediction<TAction, TState> prediction) {
        searchNode.getEdge().setProbableReward(prediction.getValue());
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> void initializeExplorationProbabilities(final AlphaZeroPrediction<TAction, TState> prediction) {
        List<SearchNode<TAction, AlphaZeroEdge, TState>> explorableChildren = prediction.getExplorableChildren();
        float[] policies = prediction.getPolicies();

        for (int i = 0, c = explorableChildren.size(); i < c; i++) {
            explorableChildren.get(i).getEdge().setExplorationProbability(policies[i]);
        }
    }

    @Override
    public void expand(final SearchNode<TAction, AlphaZeroEdge, TState> searchNode) {
        AlphaZeroPrediction<TAction, TState> prediction = traversalModel.predict(searchNode, edgeFactory);

        initializeProbableReward(searchNode, prediction);
        initializeExplorationProbabilities(prediction);
        searchNode.setUnexploredChildren(List.of());
        searchNode.setExplorableChildren(prediction.getExplorableChildren());
        searchNode.setFullyExploredChildren(new ArrayList<>());

        if (additionalExpander != null) {
            additionalExpander.expand(searchNode);
        }
    }
}
