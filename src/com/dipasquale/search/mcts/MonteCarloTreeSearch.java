package com.dipasquale.search.mcts;

public interface MonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> {
    int INITIAL_ACTION_ID = -1;
    int INITIAL_PARTICIPANT_ID = -1;
    int IN_PROGRESS_STATUS_ID = 0;
    int DRAWN_STATUS_ID = -1;

    TAction proposeNextAction(TState state);

    void reset();
}
