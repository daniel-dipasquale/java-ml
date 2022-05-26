package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.Edge;

public interface ClassicEdge extends Edge {
    void increaseVisited();

    void increaseUnfinished();

    int getWon();

    void increaseWon();

    int getDrawn();

    void increaseDrawn();
}
