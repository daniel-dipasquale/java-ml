package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.float2.MultivariateDistributionSupport;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchNodeProvider;
import com.dipasquale.search.mcts.core.State;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroExplorationProbabilityPerturber<TAction extends Action, TState extends State<TAction, TState>> implements SearchNodeProvider<TAction, AlphaZeroEdge, TState> {
    private static final double DEFAULT_SHAPE = 0.5D;
    private static final float EPSILON = 0.25f;
    private final MultivariateDistributionSupport multivariateDistributionSupport;

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> provide(final TState state) {
        return null;
    }

    @Override
    public boolean registerIfApplicable(final SearchNode<TAction, AlphaZeroEdge, TState> node) {
        if (node.getParent() != null) {
            return false;
        }

        double[] shapes = node.getExplorableChildren().stream()
                .mapToDouble(childNode -> DEFAULT_SHAPE)
                .toArray();

        double[] noises = multivariateDistributionSupport.nextRandom(shapes);
        List<SearchNode<TAction, AlphaZeroEdge, TState>> childNodes = node.getExplorableChildren();
        int size = childNodes.size();
        float explorationProbabilityTotal = 0f;

        for (int i = 0; i < size; i++) {
            SearchNode<TAction, AlphaZeroEdge, TState> childNode = childNodes.get(i);
            AlphaZeroEdge childEdge = childNode.getEdge();
            float explorationProbability = childEdge.getExplorationProbability();
            float explorationProbabilityFixed = (1f - EPSILON) * explorationProbability + EPSILON * (float) noises[i];

            childEdge.setExplorationProbability(explorationProbabilityFixed);
            explorationProbabilityTotal += explorationProbabilityFixed;
        }

        for (int i = 0; i < size; i++) {
            SearchNode<TAction, AlphaZeroEdge, TState> childNode = childNodes.get(i);
            AlphaZeroEdge childEdge = childNode.getEdge();
            float explorationProbability = childEdge.getExplorationProbability();

            childEdge.setExplorationProbability(explorationProbability / explorationProbabilityTotal);
        }

        return true;
    }
}
