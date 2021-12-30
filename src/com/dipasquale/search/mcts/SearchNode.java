package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchNode<T extends State> {
    private static final int NO_CHILD_SELECTION_INDEX = -1;
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
    @Setter(AccessLevel.PACKAGE)
    private List<SearchNode<T>> unexploredChildren;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private List<SearchNode<T>> explorableChildren;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private List<SearchNode<T>> fullyExploredChildren;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private int childSelectionIndex;

    private SearchNode(final SearchNode<T> parent, final T state) {
        this.parent = parent;
        this.owner = parent;
        this.visited = parent.visited;
        this.won = parent.won;
        this.drawn = parent.drawn;
        this.environment = null;
        this.state = state;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.childSelectionIndex = NO_CHILD_SELECTION_INDEX;
    }

    SearchNode(final Environment<T> environment) {
        this.parent = null;
        this.owner = this;
        this.visited = 0;
        this.won = 0;
        this.drawn = 0;
        this.environment = environment;
        this.state = environment.getCurrentState();
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.childSelectionIndex = NO_CHILD_SELECTION_INDEX;
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

    void initializeEnvironment() {
        Environment<T> environment = owner.environment.accept(state);

        setEnvironment(environment);
    }

    boolean isExpanded() {
        return unexploredChildren != null;
    }

    boolean isFullyExplored() {
        return unexploredChildren != null && unexploredChildren.isEmpty() && explorableChildren.isEmpty();
    }
}
