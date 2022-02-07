package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.EdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchNodeCache;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroChildrenInitializerTraversalPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, AlphaZeroEdge, TState> {
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final AlphaZeroHeuristic<TAction, TState> heuristic;
    private final SearchNodeCache<TAction, AlphaZeroEdge, TState> nodeCache;

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> next(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> node) {
        if (!node.isExpanded()) {
            List<SearchNode<TAction, AlphaZeroEdge, TState>> childNodes = node.createAllPossibleChildNodes(edgeFactory);
            int size = childNodes.size();
            AlphaZeroPrediction prediction = heuristic.predict(node, size);

            for (int i = 0; i < size; i++) {
                float priorProbability = prediction.getPolicy(i);

                childNodes.get(i).getEdge().setExplorationProbability(priorProbability);
            }

            node.getEdge().setProbableReward(prediction.getValue());
            node.setUnexploredChildren(List.of());
            node.setExplorableChildren(childNodes);
            node.setFullyExploredChildren(new ArrayList<>());

            if (nodeCache != null) {
                nodeCache.addChildrenIfApplicable(node);
            }
        }

        return null;
    }
}
