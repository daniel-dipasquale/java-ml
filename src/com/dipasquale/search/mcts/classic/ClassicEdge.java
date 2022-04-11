package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.Edge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ClassicEdge implements Edge {
    private int visited = 0;
    @Getter
    private int won = 0;
    @Getter
    private int drawn = 0;
    private int unfinished = 0;
    @Getter
    @Setter
    private float explorationProbability = 1f;

    @Override
    public int getVisited() {
        return visited - unfinished;
    }

    public void increaseVisited() {
        visited++;
    }

    public void increaseWon() {
        won++;
    }

    public void increaseDrawn() {
        drawn++;
    }

    public void increaseUnfinished() {
        unfinished++;
    }
}
