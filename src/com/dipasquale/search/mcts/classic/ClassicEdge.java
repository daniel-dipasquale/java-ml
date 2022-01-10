package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.Edge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class ClassicEdge implements Edge {
    private int visited = 0;
    private int won = 0;
    private int drawn = 0;
    private int unfinished = 0;

    void increaseVisited() {
        visited++;
    }

    void increaseWon() {
        won++;
    }

    void increaseDrawn() {
        drawn++;
    }

    void increaseUnfinished() {
        unfinished++;
    }
}
