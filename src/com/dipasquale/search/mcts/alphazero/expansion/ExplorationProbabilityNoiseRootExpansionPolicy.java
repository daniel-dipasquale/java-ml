package com.dipasquale.search.mcts.alphazero.expansion;

import com.dipasquale.common.random.float2.MultivariateDistributionSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ExplorationProbabilityNoiseRootExpansionPolicy<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> {
    static final float DEFAULT_SHAPE = 0.5f;
    static final float DEFAULT_EPSILON = 0.25f;
    private final double shape;
    private final MultivariateDistributionSupport multivariateDistributionSupport;
    private final float epsilon;

    public ExplorationProbabilityNoiseRootExpansionPolicy(final MultivariateDistributionSupport multivariateDistributionSupport) {
        this(DEFAULT_SHAPE, multivariateDistributionSupport, DEFAULT_EPSILON);
    }

    @Override
    public void expand(final TSearchNode searchNode) {
        if (searchNode.getParent() != null) {
            return;
        }

        double[] shapes = searchNode.getExplorableChildren().stream()
                .mapToDouble(childSearchNode -> shape)
                .toArray();

        double[] noises = multivariateDistributionSupport.nextRandom(shapes);
        SearchNodeGroup<TAction, AlphaZeroEdge, TState, TSearchNode> childSearchNodes = searchNode.getExplorableChildren();
        int size = childSearchNodes.size();
        float totalExplorationProbability = 0f;

        for (int i = 0; i < size; i++) {
            TSearchNode childSearchNode = childSearchNodes.getByIndex(i);
            AlphaZeroEdge childEdge = childSearchNode.getEdge();
            float explorationProbability = childEdge.getExplorationProbability();
            float fixedExplorationProbability = (1f - epsilon) * explorationProbability + epsilon * (float) noises[i];

            childEdge.setExplorationProbability(fixedExplorationProbability);
            totalExplorationProbability += fixedExplorationProbability;
        }

        if (Float.compare(totalExplorationProbability, 1f) != 0) { // NOTE: this should always be false, unless floating point arithmetic problems are found
            for (TSearchNode childSearchNode : childSearchNodes) {
                AlphaZeroEdge childEdge = childSearchNode.getEdge();
                float explorationProbability = childEdge.getExplorationProbability();

                childEdge.setExplorationProbability(explorationProbability / totalExplorationProbability);
            }
        }
    }
}
