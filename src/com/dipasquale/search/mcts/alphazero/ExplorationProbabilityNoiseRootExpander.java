package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.float2.MultivariateDistributionSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Expander;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ExplorationProbabilityNoiseRootExpander<TAction extends Action, TState extends State<TAction, TState>> implements Expander<TAction, AlphaZeroEdge, TState> {
    static final float DEFAULT_SHAPE = 0.5f;
    static final float DEFAULT_EPSILON = 0.25f;
    private final double shape;
    private final MultivariateDistributionSupport multivariateDistributionSupport;
    private final float epsilon;

    public ExplorationProbabilityNoiseRootExpander(final MultivariateDistributionSupport multivariateDistributionSupport) {
        this(DEFAULT_SHAPE, multivariateDistributionSupport, DEFAULT_EPSILON);
    }

    @Override
    public void expand(final SearchNode<TAction, AlphaZeroEdge, TState> searchNode) {
        if (searchNode.getParent() != null) {
            return;
        }

        double[] shapes = searchNode.getExplorableChildren().stream()
                .mapToDouble(childSearchNode -> shape)
                .toArray();

        double[] noises = multivariateDistributionSupport.nextRandom(shapes);
        List<SearchNode<TAction, AlphaZeroEdge, TState>> childSearchNodes = searchNode.getExplorableChildren();
        int size = childSearchNodes.size();
        float explorationProbabilityTotal = 0f;

        for (int i = 0; i < size; i++) {
            SearchNode<TAction, AlphaZeroEdge, TState> childSearchNode = childSearchNodes.get(i);
            AlphaZeroEdge childEdge = childSearchNode.getEdge();
            float explorationProbability = childEdge.getExplorationProbability();
            float explorationProbabilityFixed = (1f - epsilon) * explorationProbability + epsilon * (float) noises[i];

            childEdge.setExplorationProbability(explorationProbabilityFixed);
            explorationProbabilityTotal += explorationProbabilityFixed;
        }

        if (Float.compare(explorationProbabilityTotal, 1f) != 0) { // NOTE: this should always be false, unless floating point arithmetic problems are found
            for (SearchNode<TAction, AlphaZeroEdge, TState> childSearchNode : childSearchNodes) {
                AlphaZeroEdge childEdge = childSearchNode.getEdge();
                float explorationProbability = childEdge.getExplorationProbability();

                childEdge.setExplorationProbability(explorationProbability / explorationProbabilityTotal);
            }
        }
    }
}
