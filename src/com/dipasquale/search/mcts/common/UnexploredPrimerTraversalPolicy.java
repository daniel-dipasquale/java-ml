package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnexploredPrimerTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private static final UnexploredPrimerTraversalPolicy<?, ?, ?, ?> INSTANCE = new UnexploredPrimerTraversalPolicy<>();

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> UnexploredPrimerTraversalPolicy<TAction, TEdge, TState, TSearchNode> getInstance() {
        return (UnexploredPrimerTraversalPolicy<TAction, TEdge, TState, TSearchNode>) INSTANCE;
    }

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        List<TSearchNode> unexploredChildren = searchNode.getUnexploredChildren();
        int size = unexploredChildren.size();

        if (size == 0) {
            return null;
        }

        TSearchNode childSearchNode = unexploredChildren.remove(size - 1);
        List<TSearchNode> explorableChildren = searchNode.getExplorableChildren();

        explorableChildren.add(childSearchNode);
        childSearchNode.initializeState();
        searchNode.setSelectedExplorableChildIndex(explorableChildren.size() - 1);

        return childSearchNode;
    }
}
