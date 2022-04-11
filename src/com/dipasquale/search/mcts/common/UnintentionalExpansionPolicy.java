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
public final class UnintentionalExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final EdgeFactory<TEdge> edgeFactory;
    private final ExplorationHeuristic<TAction> explorationHeuristic;

    @Override
    public void expand(final TSearchNode searchNode) {
        List<TSearchNode> explorableChildren = searchNode.createAllPossibleChildNodes(edgeFactory);

        for (TSearchNode explorableChild : explorableChildren) {
            float explorationProbability = explorationHeuristic.estimate(explorableChild.getAction());

            explorableChild.getEdge().setExplorationProbability(explorationProbability);
        }

        searchNode.setUnexploredChildren(List.of());
        searchNode.setExplorableChildren(explorableChildren);
        searchNode.setFullyExploredChildren(new ArrayList<>());
    }
}
