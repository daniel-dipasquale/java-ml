package com.dipasquale.search.mcts.propagation;

import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
public interface BackPropagationObserver<TAction, TState extends State<TAction, TState>> {
    void notify(int statusId, Iterable<SearchResult<TAction, TState>> results);
}
