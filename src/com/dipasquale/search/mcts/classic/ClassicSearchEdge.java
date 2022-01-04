package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.SearchEdge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class ClassicSearchEdge implements SearchEdge {
    private int visited = 0;
    private int won = 0;
    private int drawn = 0;

    void increaseVisited() {
        visited++;
    }

    void increaseWon() {
        won++;
    }

    void increaseDrawn() {
        drawn++;
    }
}
