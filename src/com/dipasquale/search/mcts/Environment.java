package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public final class Environment<TAction extends Action, TState extends State<TAction, TState>, TParticipant extends Participant<TAction, TState>> {
    private static final Inspector<?, ?> DEFAULT_INSPECTOR = new Inspector<>();
    private static final boolean DEBUG = false;
    private final ObjectFactory<TState> initialStateFactory;
    private final Consumer<SearchNodeResult<TAction, TState>> inspector;
    private final TParticipant[] participants;

    public Environment(final ObjectFactory<TState> initialStateFactory, final TParticipant[] participants) {
        this(initialStateFactory, (Consumer<SearchNodeResult<TAction, TState>>) (Object) DEFAULT_INSPECTOR, participants);
    }

    public TState interact() {
        TState state = initialStateFactory.create();
        SearchNodeResult<TAction, TState> searchNodeResult = SearchNodeResult.createRoot(state);

        if (DEBUG) {
            inspector.accept(searchNodeResult);
        }

        for (int i = 0; state.getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID; i = state.getNextParticipantId() - 1) {
            searchNodeResult = participants[i].produceNext(searchNodeResult);
            state = searchNodeResult.getState();

            if (DEBUG) {
                inspector.accept(searchNodeResult);
            }
        }

        for (TParticipant participant : participants) {
            participant.accept(searchNodeResult);
        }

        return state;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Inspector<TAction extends Action, TState extends State<TAction, TState>> implements Consumer<SearchNodeResult<TAction, TState>> {
        @Override
        public void accept(final SearchNodeResult<TAction, TState> searchNodeResult) {
        }
    }
}
