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
    private final Consumer<SearchResult<TAction, TState>> inspector;
    private final TParticipant[] participants;

    public Environment(final ObjectFactory<TState> initialStateFactory, final TParticipant[] participants) {
        this(initialStateFactory, (Consumer<SearchResult<TAction, TState>>) (Object) DEFAULT_INSPECTOR, participants);
    }

    public TState interact() {
        TState state = initialStateFactory.create();
        SearchResult<TAction, TState> searchResult = SearchResult.createRoot(state);

        if (DEBUG) {
            inspector.accept(searchResult);
        }

        for (int i = 0; state.getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID; i = state.getNextParticipantId() - 1) {
            searchResult = participants[i].produceNext(searchResult);
            state = searchResult.getState();

            if (DEBUG) {
                inspector.accept(searchResult);
            }
        }

        for (TParticipant participant : participants) {
            participant.accept(searchResult);
        }

        return state;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Inspector<TAction extends Action, TState extends State<TAction, TState>> implements Consumer<SearchResult<TAction, TState>> {
        @Override
        public void accept(final SearchResult<TAction, TState> searchResult) {
        }
    }
}
