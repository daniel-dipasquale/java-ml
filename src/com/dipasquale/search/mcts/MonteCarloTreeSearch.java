package com.dipasquale.search.mcts;

public interface MonteCarloTreeSearch<TAction, TState extends State<TAction, TState>> {
    int ROOT_ACTION_ID = -1;
    int IN_PROGRESS_STATUS_ID = 0;
    int DRAWN_STATUS_ID = -1;

    SearchResult<TAction, TState> proposeNext(SearchResult<TAction, TState> searchResult);

    default SearchResult<TAction, TState> proposeFirst(final TState state) {
        SearchResult<TAction, TState> searchResult = SearchResult.createRoot(state);

        return proposeNext(searchResult);
    }

    void reset();
}
