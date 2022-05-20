package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnexploredPrimerTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private static final UnexploredPrimerTraversalPolicy<?, ?, ?, ?> INSTANCE = new UnexploredPrimerTraversalPolicy<>();

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> UnexploredPrimerTraversalPolicy<TAction, TEdge, TState, TSearchNode> getInstance() {
        return (UnexploredPrimerTraversalPolicy<TAction, TEdge, TState, TSearchNode>) INSTANCE;
    }

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren = searchNode.getUnexploredChildren();
        int size = unexploredChildren.size();

        if (size == 0) {
            return null;
        }

        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = searchNode.getExplorableChildren();
        TSearchNode childSearchNode = unexploredChildren.removeByIndex(size - 1);
        int explorableChildKey = explorableChildren.add(childSearchNode);
        searchNode.setSelectedExplorableChildKey(explorableChildKey);

        return childSearchNode;
    }
}
