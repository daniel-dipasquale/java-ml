package com.dipasquale.search.mcts;

public interface MonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> {
    int ROOT_ACTION_ID = -1;
    int IN_PROGRESS_STATUS_ID = 0;
    int DRAWN_STATUS_ID = -1;

    SearchNodeResult<TAction, TState> proposeNext(SearchNodeResult<TAction, TState> searchNodeResult);

    default SearchNodeResult<TAction, TState> proposeFirst(final TState state) {
        SearchNodeResult<TAction, TState> searchNodeResult = SearchNodeResult.createRoot(state);

        return proposeNext(searchNodeResult);
    }

    void reset();
}
