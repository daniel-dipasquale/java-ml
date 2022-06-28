package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class SearchResult<TAction, TState extends State<TAction, TState>> {
    private int actionId;
    private TAction action;
    private final StateId stateId;
    private TState state;

    static <TAction, TState extends State<TAction, TState>> SearchResult<TAction, TState> createRoot(final TState state) {
        return new SearchResult<>(MonteCarloTreeSearch.ROOT_ACTION_ID, null, new StateId(), state);
    }

    void reinitialize(final SearchResult<TAction, TState> result) {
        actionId = result.actionId;
        action = result.action;
        state = result.state;
        stateId.reinitialize(result.stateId);
    }

    public SearchResult<TAction, TState> createChild(final int actionId, final TAction action) {
        StateId childStateId = stateId.createChild(actionId);
        TState childState = state.accept(action);

        return new SearchResult<>(actionId, action, childStateId, childState);
    }
}
