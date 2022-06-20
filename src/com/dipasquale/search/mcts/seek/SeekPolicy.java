package com.dipasquale.search.mcts.seek;

public interface SeekPolicy {
    void begin();

    int getMaximumSelectionCount();

    void end();
}
