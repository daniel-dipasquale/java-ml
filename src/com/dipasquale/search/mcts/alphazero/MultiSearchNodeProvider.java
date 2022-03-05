package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeProvider;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MultiSearchNodeProvider<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeProvider<TAction, TEdge, TState> {
    private final List<SearchNodeProvider<TAction, TEdge, TState>> searchNodeProviders;

    @Override
    public SearchNode<TAction, TEdge, TState> provide(final TState state) {
        SearchNode<TAction, TEdge, TState> node = null;

        for (SearchNodeProvider<TAction, TEdge, TState> nodeProvider : searchNodeProviders) {
            SearchNode<TAction, TEdge, TState> temporaryNode = nodeProvider.provide(state);

            if (temporaryNode != null) {
                node = temporaryNode;
            }
        }

        return node;
    }

    @Override
    public boolean registerIfApplicable(final SearchNode<TAction, TEdge, TState> node) {
        boolean registered = true;

        for (SearchNodeProvider<TAction, TEdge, TState> searchNodeProvider : searchNodeProviders) {
            registered &= searchNodeProvider.registerIfApplicable(node);
        }

        return registered;
    }
}
