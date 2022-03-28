package com.dipasquale.search.mcts.common;

import com.dipasquale.common.random.float1.RandomSupport;
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
public final class IntentionalExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ExpansionPolicy<TAction, TEdge, TState> {
    private final EdgeFactory<TEdge> edgeFactory;
    private final RandomSupport randomSupport;

    @Override
    public void expand(final SearchNode<TAction, TEdge, TState> searchNode) {
        List<SearchNode<TAction, TEdge, TState>> unexploredChildren = searchNode.createAllPossibleChildNodes(edgeFactory);

        randomSupport.shuffle(unexploredChildren);
        searchNode.setUnexploredChildren(unexploredChildren);
        searchNode.setExplorableChildren(new ArrayList<>());
        searchNode.setFullyExploredChildren(new ArrayList<>());
    }
}
