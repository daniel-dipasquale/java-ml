package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public final class Environment<TAction extends Action, TState extends State<TAction, TState>, TParticipant extends Participant<TAction, TState>> {
    private static final Consumer<?> DEFAULT_INSPECTOR = state -> {
    };

    private static final boolean DEBUG = false;
    private final ObjectFactory<TState> initialStateFactory;
    private final TParticipant[] participants;
    private final Consumer<TState> inspector;

    public Environment(final ObjectFactory<TState> initialStateFactory, final TParticipant[] participants) {
        this(initialStateFactory, participants, (Consumer<TState>) DEFAULT_INSPECTOR);
    }

    public TState interact() {
        TState state = initialStateFactory.create();
        int statusId = state.getStatusId();

        for (int i = 0; statusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID; i = state.getNextParticipantId() - 1) {
            TAction action = participants[i].createNextAction(state);

            state = state.accept(action);
            statusId = state.getStatusId();

            if (DEBUG) {
                inspector.accept(state);
            }
        }

        for (TParticipant participant : participants) {
            participant.accept(state);
        }

        return state;
    }
}
