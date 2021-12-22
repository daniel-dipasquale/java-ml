package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Node<T extends State> {
    @Getter
    private final Node<T> parent;
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

    Node(final Node<T> parent, final T state) {
        this.parent = parent;
        this.visited = parent.visited;
        this.won = parent.won;
        this.drawn = parent.drawn;
        this.state = state;
        this.environment = null;
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

    List<Node<T>> createAllPossibleChildNodes(final RandomSupport randomSupport) {
        Iterable<T> possibleStates = environment.createAllPossibleStates();

        List<Node<T>> childNodes = StreamSupport.stream(possibleStates.spliterator(), false)
                .map(s -> new Node<>(this, s))
                .collect(Collectors.toList());

        randomSupport.shuffle(childNodes);

        return childNodes;
    }

    void initializeChildren(final RandomSupport randomSupport) {
        exploredChildren = new ArrayList<>();
        unexploredChildren = createAllPossibleChildNodes(randomSupport);
    }

    void initializeEnvironment() {
        Environment<T> environment = parent.getEnvironment().accept(this);

        setEnvironment(environment);
    }
}
