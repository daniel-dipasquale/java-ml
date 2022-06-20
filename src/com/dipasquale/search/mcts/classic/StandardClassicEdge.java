package com.dipasquale.search.mcts.classic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public final class StandardClassicEdge implements ClassicEdge {
    private int visited = 0;
    private int unfinished = 0;
    @Getter
    private int won = 0;
    @Getter
    private int drawn = 0;
    @Getter
    @Setter
    private float explorationProbability = 1f;

    public StandardClassicEdge(final StandardClassicEdge edge) {
        this.visited = edge.visited;
        this.unfinished = edge.unfinished;
        this.won = edge.won;
        this.drawn = edge.drawn;
        this.explorationProbability = edge.explorationProbability;
    }

    @Override
    public int getVisited() {
        return visited - unfinished;
    }

    @Override
    public void increaseVisited() {
        visited++;
    }

    @Override
    public void increaseUnfinished() {
        unfinished++;
    }

    @Override
    public void increaseWon() {
        won++;
    }

    @Override
    public void increaseDrawn() {
        drawn++;
    }
}
