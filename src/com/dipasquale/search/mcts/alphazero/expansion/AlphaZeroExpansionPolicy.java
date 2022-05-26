package com.dipasquale.search.mcts.alphazero.expansion;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.selection.AlphaZeroModel;
import com.dipasquale.search.mcts.alphazero.selection.AlphaZeroPrediction;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AlphaZeroExpansionPolicy<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> {
    private final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel;
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final SearchNodeGroupProvider<TAction, AlphaZeroEdge, TState, TSearchNode> searchNodeGroupProvider;

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> void initializeProbableReward(final TSearchNode searchNode, final AlphaZeroPrediction<TAction, TState, TSearchNode> prediction) {
        searchNode.getEdge().setProbableReward(prediction.getValue());
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> void initializeExplorationProbabilities(final AlphaZeroPrediction<TAction, TState, TSearchNode> prediction) {
        SearchNodeGroup<TAction, AlphaZeroEdge, TState, TSearchNode> explorableChildren = prediction.getExplorableChildren();
        float[] policies = prediction.getPolicies();

        for (int i = 0, c = explorableChildren.size(); i < c; i++) {
            TSearchNode searchNode = explorableChildren.getByIndex(i);

            searchNode.getEdge().setExplorationProbability(policies[i]);
        }
    }

    @Override
    public void expand(final TSearchNode searchNode) {
        AlphaZeroPrediction<TAction, TState, TSearchNode> prediction = traversalModel.predict(searchNode, edgeFactory, searchNodeGroupProvider);

        initializeProbableReward(searchNode, prediction);
        initializeExplorationProbabilities(prediction);
        searchNode.setUnexploredChildren(searchNodeGroupProvider.getEmpty());
        searchNode.setExplorableChildren(prediction.getExplorableChildren());
        searchNode.setFullyExploredChildren(searchNodeGroupProvider.create(null));
    }
}
