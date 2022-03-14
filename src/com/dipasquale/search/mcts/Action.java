package com.dipasquale.search.mcts;

public interface Action {
    String getCacheId();

    int getId();

    int getParticipantId();
}
