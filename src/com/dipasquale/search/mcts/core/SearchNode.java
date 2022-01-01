package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchNode<TState extends SearchState, TEdge extends SearchEdge> {
    private static final int NO_CHILD_SELECTED_INDEX = -1;
    @Getter
    private final SearchNode<TState, TEdge> parent;
    private final SearchNode<TState, TEdge> owner;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Environment<TState> environment;
    @Getter
    private final TState state;
    @Getter
    private final TEdge edge;
    @Getter
    @Setter
    private List<SearchNode<TState, TEdge>> unexploredChildren;
    @Getter
    @Setter
    private List<SearchNode<TState, TEdge>> explorableChildren;
    @Getter
    @Setter
    private List<SearchNode<TState, TEdge>> fullyExploredChildren;
    @Getter
    @Setter
    private int childSelectedIndex;

    SearchNode(final Environment<TState> environment, final TEdge edge) {
        this.parent = null;
        this.owner = this;
        this.environment = environment;
        this.state = environment.getCurrentState();
        this.edge = edge;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.childSelectedIndex = NO_CHILD_SELECTED_INDEX;
    }

    private SearchNode(final SearchNode<TState, TEdge> parent, final TState state, final TEdge edge) {
        this.parent = parent;
        this.owner = parent;
        this.environment = null;
        this.state = state;
        this.edge = edge;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.childSelectedIndex = NO_CHILD_SELECTED_INDEX;
    }

    public List<SearchNode<TState, TEdge>> createAllPossibleChildNodes(final SearchEdgeFactory<TEdge> edgeFactory) {
        Iterable<TState> possibleStates = environment.createAllPossibleStates();

        return StreamSupport.stream(possibleStates.spliterator(), false)
                .map(s -> new SearchNode<>(this, s, edgeFactory.create(edge)))
                .collect(Collectors.toList());
    }

    public void initializeEnvironment() {
        setEnvironment(owner.environment.accept(state));
    }

    public boolean isExpanded() {
        return unexploredChildren != null;
    }

    public boolean isFullyExplored() {
        return isExpanded() && unexploredChildren.isEmpty() && explorableChildren.isEmpty();
    }
}
