package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class Node<T> {
    @Getter
    private final Node<T> parent;
    @Getter
    private final int participantId;
    @Getter
    private int visited;
    @Getter
    private int won;
    @Getter
    private int drawn;
    @Getter
    private final T state;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private Environment<T> environment;
    @Getter(AccessLevel.PACKAGE)
    private List<Node<T>> exploredChildren;
    @Getter(AccessLevel.PACKAGE)
    private List<Node<T>> unexploredChildren;

    Node(final Node<T> parent, final int participantId, final T state) {
        this.parent = parent;
        this.participantId = participantId;
        this.visited = parent.visited;
        this.won = parent.won;
        this.drawn = parent.drawn;
        this.state = state;
        this.exploredChildren = null;
        this.unexploredChildren = null;
    }

    void addVisited() {
        visited++;
    }

    void addWon() {
        won++;
    }

    void addDrawn() {
        drawn++;
    }

    void expandChildren(final List<Node<T>> children) {
        exploredChildren = new ArrayList<>();
        unexploredChildren = children;
    }
}
