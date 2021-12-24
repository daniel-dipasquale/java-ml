package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchNode<T extends State> {
    @Getter
    private final SearchNode<T> parent;
    private final SearchNode<T> owner;
    @Getter
    private int visited;
    @Getter
    private int won;
    @Getter
    private int drawn;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PRIVATE)
    private Environment<T> environment;
    @Getter
    private final T state;
    @Getter(AccessLevel.PACKAGE)
    private List<SearchNode<T>> exploredChildren;
    @Getter(AccessLevel.PACKAGE)
    private List<SearchNode<T>> unexploredChildren;

    private SearchNode(final SearchNode<T> parent, final T state) {
        this.parent = parent;
        this.owner = parent;
        this.visited = parent.visited;
        this.won = parent.won;
        this.drawn = parent.drawn;
        this.environment = null;
        this.state = state;
        this.exploredChildren = null;
        this.unexploredChildren = null;
    }

    SearchNode(final Environment<T> environment) {
        this.parent = null;
        this.owner = this;
        this.visited = 0;
        this.won = 0;
        this.drawn = 0;
        this.environment = environment;
        this.state = environment.getCurrentState();
        this.exploredChildren = null;
        this.unexploredChildren = null;
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

    List<SearchNode<T>> createAllPossibleChildNodes() {
        Iterable<T> possibleStates = environment.createAllPossibleStates();

        return StreamSupport.stream(possibleStates.spliterator(), false)
                .map(s -> new SearchNode<>(this, s))
                .collect(Collectors.toList());
    }

    void initializeChildren(final List<SearchNode<T>> children) {
        exploredChildren = new ArrayList<>();
        unexploredChildren = children;
    }

    void initializeEnvironment() {
        Environment<T> environment = owner.environment.accept(state);

        setEnvironment(environment);
    }
}
