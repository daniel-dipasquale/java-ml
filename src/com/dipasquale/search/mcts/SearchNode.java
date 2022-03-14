package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public final class SearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    static final int NO_CHILD_SELECTED_INDEX = -1;
    private static final boolean IS_SIMULATION = true;
    private SearchNode<TAction, TEdge, TState> parent;
    @Getter(AccessLevel.NONE)
    private SearchNode<TAction, TEdge, TState> owner;
    private TAction action;
    private final TEdge edge;
    @Setter(AccessLevel.PRIVATE)
    private TState state;
    @Setter
    private List<SearchNode<TAction, TEdge, TState>> unexploredChildren;
    @Setter
    private List<SearchNode<TAction, TEdge, TState>> explorableChildren;
    @Setter
    private List<SearchNode<TAction, TEdge, TState>> fullyExploredChildren;
    @Setter
    private int selectedExplorableChildIndex;

    private SearchNode(final EdgeFactory<TEdge> edgeFactory, final TState state) {
        this.parent = null;
        this.owner = this;
        this.action = state.getLastAction();
        this.edge = edgeFactory.create(this);
        this.state = state;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    private SearchNode(final SearchNode<TAction, TEdge, TState> parent, final TAction action, final EdgeFactory<TEdge> edgeFactory) {
        this.parent = parent;
        this.owner = parent;
        this.action = action;
        this.edge = edgeFactory.create(this);
        this.state = null;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> SearchNode<TAction, TEdge, TState> createRoot(final EdgeFactory<TEdge> edgeFactory, final TState state) {
        return new SearchNode<>(edgeFactory, state);
    }

    public void reinitialize(final TState state) {
        parent = null;
        owner = this;
        action = state.getLastAction();
        setState(state);
    }

    public List<SearchNode<TAction, TEdge, TState>> createAllPossibleChildNodes(final EdgeFactory<TEdge> edgeFactory) {
        Iterable<TAction> possibleActions = state.createAllPossibleActions();

        return StreamSupport.stream(possibleActions.spliterator(), false)
                .map(action -> new SearchNode<>(this, action, edgeFactory))
                .collect(Collectors.toList());
    }

    public void initializeState() {
        TState state = owner.state.accept(action, IS_SIMULATION);

        setState(state);
    }

    public boolean isExpanded() {
        return unexploredChildren != null;
    }

    public boolean isFullyExplored() {
        return isExpanded() && unexploredChildren.isEmpty() && explorableChildren.isEmpty();
    }
}
