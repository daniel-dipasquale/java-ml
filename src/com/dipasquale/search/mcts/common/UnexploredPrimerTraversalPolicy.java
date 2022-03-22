package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnexploredPrimerTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private static final UnexploredPrimerTraversalPolicy<?, ?, ?> INSTANCE = new UnexploredPrimerTraversalPolicy<>();

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> UnexploredPrimerTraversalPolicy<TAction, TEdge, TState> getInstance() {
        return (UnexploredPrimerTraversalPolicy<TAction, TEdge, TState>) INSTANCE;
    }

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> searchNode) {
        List<SearchNode<TAction, TEdge, TState>> unexploredChildren = searchNode.getUnexploredChildren();
        int size = unexploredChildren.size();

        if (size == 0) {
            return null;
        }

        SearchNode<TAction, TEdge, TState> childSearchNode = unexploredChildren.remove(size - 1);
        List<SearchNode<TAction, TEdge, TState>> explorableChildren = searchNode.getExplorableChildren();

        explorableChildren.add(childSearchNode);
        childSearchNode.initializeState();
        searchNode.setSelectedExplorableChildIndex(explorableChildren.size() - 1);

        return childSearchNode;
    }
}
