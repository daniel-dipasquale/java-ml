package com.dipasquale.search.mcts;

import com.dipasquale.search.mcts.buffer.TreeId;

public interface Action {
    TreeId getTreeId();

    int getId();
}
