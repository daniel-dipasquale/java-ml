package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchNode<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> {
    private static final int NO_CHILD_SELECTED_INDEX = -1;
    @Getter
    private final SearchNode<TState, TEdge, TEnvironment> parent;
    @Getter
    private final int depth;
    private final SearchNode<TState, TEdge, TEnvironment> owner;
    @Getter
    private final TState state;
    @Getter
    private final TEdge edge;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private TEnvironment environment;
    @Getter
    @Setter
    private List<SearchNode<TState, TEdge, TEnvironment>> unexploredChildren;
    @Getter
    @Setter
    private List<SearchNode<TState, TEdge, TEnvironment>> explorableChildren;
    @Getter
    @Setter
    private List<SearchNode<TState, TEdge, TEnvironment>> fullyExploredChildren;
    @Getter
    @Setter
    private int selectedExplorableChildIndex;

    SearchNode(final TEnvironment environment, final TEdge edge) {
        this.parent = null;
        this.depth = 0;
        this.owner = this;
        this.state = environment.getCurrentState();
        this.edge = edge;
        this.environment = environment;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    private SearchNode(final SearchNode<TState, TEdge, TEnvironment> parent, final TState state, final TEdge edge) {
        this.parent = parent;
        this.depth = parent.depth + 1;
        this.owner = parent;
        this.state = state;
        this.edge = edge;
        this.environment = null;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    public List<SearchNode<TState, TEdge, TEnvironment>> createAllPossibleChildNodes(final EdgeFactory<TEdge> edgeFactory) {
        Iterable<TState> possibleStates = environment.createAllPossibleStates();

        return StreamSupport.stream(possibleStates.spliterator(), false)
                .map(state -> new SearchNode<>(this, state, edgeFactory.create()))
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
