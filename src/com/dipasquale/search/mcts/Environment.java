package com.dipasquale.search.mcts;

public interface Environment<T> {
    int getStatusId();

    float[] getScoreEstimates();

    int getNextParticipantId();

    Node<T> getCurrentNode();

    Iterable<T> createAllPossibleStates();

    Environment<T> accept(Node<T> node);
}
