package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.EdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchNodeProvider;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class AlphaZeroChildrenInitializerTraversalPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, AlphaZeroEdge, TState> {
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
    private final AlphaZeroHeuristic<TAction, TState> heuristic;
    private final SearchNodeProvider<TAction, AlphaZeroEdge, TState> nodeProvider;

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> next(final int simulations, final SearchNode<TAction, AlphaZeroEdge, TState> node) {
        if (!node.isExpanded()) {
            AlphaZeroPrediction<TAction, TState> prediction = heuristic.predict(node, edgeFactory);
            List<SearchNode<TAction, AlphaZeroEdge, TState>> childNodes = prediction.getNodes();
            float[] policies = prediction.getPolicies();

            for (int i = 0, c = childNodes.size(); i < c; i++) {
                childNodes.get(i).getEdge().setExplorationProbability(policies[i]);
            }

            node.getEdge().setProbableReward(prediction.getValue());
            node.setUnexploredChildren(List.of());
            node.setExplorableChildren(childNodes);
            node.setFullyExploredChildren(new ArrayList<>());

            if (nodeProvider != null) {
                nodeProvider.registerIfApplicable(node);
            }
        }

        return null;
    }
}
