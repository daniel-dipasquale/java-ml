package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.SearchEdge;
import lombok.Getter;

@Getter
public final class ClassicSearchEdge implements SearchEdge {
    private int visited;
    private int won;
    private int drawn;

    ClassicSearchEdge() {
        this.visited = 0;
        this.won = 0;
        this.drawn = 0;
    }

    ClassicSearchEdge(final ClassicSearchEdge parent) {
        this.visited = parent.visited;
        this.won = parent.won;
        this.drawn = parent.drawn;
    }

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
