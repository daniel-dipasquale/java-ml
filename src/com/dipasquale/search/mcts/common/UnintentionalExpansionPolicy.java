package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class UnintentionalExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ExpansionPolicy<TAction, TEdge, TState> {
    private final EdgeFactory<TEdge> edgeFactory;
    private final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator;

    @Override
    public void expand(final SearchNode<TAction, TEdge, TState> searchNode) {
        List<SearchNode<TAction, TEdge, TState>> explorableChildren = searchNode.createAllPossibleChildNodes(edgeFactory);

        for (SearchNode<TAction, TEdge, TState> explorableChild : explorableChildren) {
            float explorationProbability = explorationProbabilityCalculator.calculate(explorableChild.getAction());

            explorableChild.getEdge().setExplorationProbability(explorationProbability);
        }

        searchNode.setUnexploredChildren(List.of());
        searchNode.setExplorableChildren(explorableChildren);
        searchNode.setFullyExploredChildren(new ArrayList<>());
    }
}
