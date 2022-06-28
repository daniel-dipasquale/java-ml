package com.dipasquale.search.mcts;

public interface Participant<TAction, TState extends State<TAction, TState>> {
    SearchResult<TAction, TState> produceNext(SearchResult<TAction, TState> searchResult);

    void accept(SearchResult<TAction, TState> searchResult);
}
