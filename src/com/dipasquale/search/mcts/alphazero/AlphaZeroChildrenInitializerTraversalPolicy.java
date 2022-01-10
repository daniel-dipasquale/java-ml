package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.EdgeFactory;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroChildrenInitializerTraversalPolicy<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> implements TraversalPolicy<TState, AlphaZeroEdge, TEnvironment> {
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final AlphaZeroHeuristic<TState, TEnvironment> heuristic;

    @Override
    public SearchNode<TState, AlphaZeroEdge, TEnvironment> next(final int simulations, final SearchNode<TState, AlphaZeroEdge, TEnvironment> node) {
        if (!node.isExpanded()) {
            List<SearchNode<TState, AlphaZeroEdge, TEnvironment>> childNodes = node.createAllPossibleChildNodes(edgeFactory);
            int childNodesSize = childNodes.size();
            AlphaZeroPrediction prediction = heuristic.predict(node, childNodesSize);

            for (int i = 0; i < childNodesSize; i++) {
                float priorProbability = prediction.getPolicy(i);

                childNodes.get(i).getEdge().setExplorationProbability(priorProbability);
            }

            node.getEdge().setProbableReward(prediction.getValue());
            node.setUnexploredChildren(List.of());
            node.setExplorableChildren(childNodes);
            node.setFullyExploredChildren(new ArrayList<>());
        }

        return null;
    }
}
