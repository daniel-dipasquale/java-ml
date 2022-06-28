package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public final class Environment<TAction, TState extends State<TAction, TState>, TParticipant extends Participant<TAction, TState>> {
    private final ObjectFactory<TState> initialStateFactory;
    private final TParticipant[] participants;
    private final Consumer<SearchResult<TAction, TState>> inspector;

    private void inspect(final SearchResult<TAction, TState> searchResult) {
        if (inspector != null) {
            inspector.accept(searchResult);
        }
    }

    public TState interact() {
        TState state = initialStateFactory.create();
        SearchResult<TAction, TState> searchResult = SearchResult.createRoot(state);

        inspect(searchResult);

        for (int i = 0; state.getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID; i = state.getNextParticipantId() - 1) {
            searchResult = participants[i].produceNext(searchResult);
            state = searchResult.getState();
            inspect(searchResult);
        }

        for (TParticipant participant : participants) {
            participant.accept(searchResult);
        }

        return state;
    }
}
