package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class SearchNodeResult<TAction extends Action, TState extends State<TAction, TState>> {
    private final TAction action;
    private final TState state;
    private final StateId stateId;

    static <TAction extends Action, TState extends State<TAction, TState>> SearchNodeResult<TAction, TState> createRoot(final TState state) {
        return new SearchNodeResult<>(state.createRootAction(), state, new StateId());
    }

    public SearchNodeResult<TAction, TState> createChild(final TAction action) {
        TState childState = state.accept(action);
        StateId childStateId = stateId.createChild(action.getId());

        return new SearchNodeResult<>(action, childState, childStateId);
    }
}
