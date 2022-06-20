package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class SearchResult<TAction extends Action, TState extends State<TAction, TState>> {
    private TAction action;
    private TState state;
    private final StateId stateId;

    static <TAction extends Action, TState extends State<TAction, TState>> SearchResult<TAction, TState> createRoot(final TState state) {
        return new SearchResult<>(state.createRootAction(), state, new StateId());
    }

    void reinitialize(final SearchResult<TAction, TState> result) {
        action = result.action;
        state = result.state;
        stateId.reinitialize(result.stateId);
    }

    public SearchResult<TAction, TState> createChild(final TAction action) {
        TState childState = state.accept(action);
        StateId childStateId = stateId.createChild(action.getId());

        return new SearchResult<>(action, childState, childStateId);
    }
}
