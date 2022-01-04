package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchEdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroChildrenInitializerSelectionPolicy<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> implements SelectionPolicy<TState, AlphaZeroSearchEdge, TEnvironment> {
    private final SearchEdgeFactory<AlphaZeroSearchEdge> edgeFactory;
    private final AlphaZeroHeuristic<TState, TEnvironment> heuristic;

    @Override
    public SearchNode<TState, AlphaZeroSearchEdge, TEnvironment> next(final int simulations, final SearchNode<TState, AlphaZeroSearchEdge, TEnvironment> node) {
        if (!node.isExpanded()) {
            List<SearchNode<TState, AlphaZeroSearchEdge, TEnvironment>> childNodes = node.createAllPossibleChildNodes(edgeFactory);
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
