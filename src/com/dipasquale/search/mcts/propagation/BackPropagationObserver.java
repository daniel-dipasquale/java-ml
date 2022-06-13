package com.dipasquale.search.mcts.propagation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNodeResult;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
public interface BackPropagationObserver<TAction extends Action, TState extends State<TAction, TState>> {
    void notify(int statusId, Iterable<SearchNodeResult<TAction, TState>> results);
}
