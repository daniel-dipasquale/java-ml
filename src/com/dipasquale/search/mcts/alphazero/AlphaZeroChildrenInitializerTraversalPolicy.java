package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeInitializer;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroChildrenInitializerTraversalPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, AlphaZeroEdge, TState> {
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final AlphaZeroModel<TAction, TState> traversalModel;
    private final SearchNodeInitializer<TAction, AlphaZeroEdge, TState> nodeInitializer;

    private static <TAction extends Action, TState extends State<TAction, TState>> void initializeProbableReward(final SearchNode<TAction, AlphaZeroEdge, TState> node, final AlphaZeroPrediction<TAction, TState> prediction) {
        node.getEdge().setProbableReward(prediction.getValue());
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> void initializeExplorationProbabilities(final AlphaZeroPrediction<TAction, TState> prediction) {
        List<SearchNode<TAction, AlphaZeroEdge, TState>> childNodes = prediction.getNodes();
        float[] policies = prediction.getPolicies();

        for (int i = 0, c = childNodes.size(); i < c; i++) {
            childNodes.get(i).getEdge().setExplorationProbability(policies[i]);
        }
    }

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> next(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> node) {
        if (!node.isExpanded()) {
            AlphaZeroPrediction<TAction, TState> prediction = traversalModel.predict(node, edgeFactory);

            initializeProbableReward(node, prediction);
            initializeExplorationProbabilities(prediction);
            node.setUnexploredChildren(List.of());
            node.setExplorableChildren(prediction.getNodes());
            node.setFullyExploredChildren(new ArrayList<>());

            if (nodeInitializer != null) {
                nodeInitializer.apply(node);
            }
        }

        return null;
    }
}
