package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.float2.MultivariateDistributionSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeInitializer;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ExplorationProbabilityNoiseRootSearchNodeInitializer<TAction extends Action, TState extends State<TAction, TState>> implements SearchNodeInitializer<TAction, AlphaZeroEdge, TState> {
    static final float DEFAULT_SHAPE = 0.5f;
    static final float DEFAULT_EPSILON = 0.25f;
    private final double shape;
    private final MultivariateDistributionSupport multivariateDistributionSupport;
    private final float epsilon;

    public ExplorationProbabilityNoiseRootSearchNodeInitializer(final MultivariateDistributionSupport multivariateDistributionSupport) {
        this(DEFAULT_SHAPE, multivariateDistributionSupport, DEFAULT_EPSILON);
    }

    @Override
    public boolean apply(final SearchNode<TAction, AlphaZeroEdge, TState> node) {
        if (node.getParent() != null) {
            return false;
        }

        double[] shapes = node.getExplorableChildren().stream()
                .mapToDouble(childNode -> shape)
                .toArray();

        double[] noises = multivariateDistributionSupport.nextRandom(shapes);
        List<SearchNode<TAction, AlphaZeroEdge, TState>> childNodes = node.getExplorableChildren();
        int size = childNodes.size();
        float explorationProbabilityTotal = 0f;

        for (int i = 0; i < size; i++) {
            SearchNode<TAction, AlphaZeroEdge, TState> childNode = childNodes.get(i);
            AlphaZeroEdge childEdge = childNode.getEdge();
            float explorationProbability = childEdge.getExplorationProbability();
            float explorationProbabilityFixed = (1f - epsilon) * explorationProbability + epsilon * (float) noises[i];

            childEdge.setExplorationProbability(explorationProbabilityFixed);
            explorationProbabilityTotal += explorationProbabilityFixed;
        }

        for (SearchNode<TAction, AlphaZeroEdge, TState> childNode : childNodes) {
            AlphaZeroEdge childEdge = childNode.getEdge();
            float explorationProbability = childEdge.getExplorationProbability();

            childEdge.setExplorationProbability(explorationProbability / explorationProbabilityTotal);
        }

        return true;
    }
}
