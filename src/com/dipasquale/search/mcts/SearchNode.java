package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public final class SearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    static final int NO_CHILD_SELECTED_INDEX = -1;
    @Getter(AccessLevel.NONE)
    private WeakReference<SearchNode<TAction, TEdge, TState>> parent;
    @Getter(AccessLevel.NONE)
    private WeakReference<SearchNode<TAction, TEdge, TState>> owner;
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

    private SearchNode(final TEdge edge, final TState state) {
        this.parent = new WeakReference<>(null);
        this.owner = new WeakReference<>(this);
        this.action = state.getLastAction();
        this.edge = edge;
        this.state = state;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    private SearchNode(final SearchNode<TAction, TEdge, TState> parent, final TAction action, final TEdge edge) {
        this.parent = new WeakReference<>(parent);
        this.owner = new WeakReference<>(parent);
        this.action = action;
        this.edge = edge;
        this.state = null;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> SearchNode<TAction, TEdge, TState> createRoot(final EdgeFactory<TEdge> edgeFactory, final TState state) {
        return new SearchNode<>(edgeFactory.create(), state);
    }

    public SearchNode<TAction, TEdge, TState> getParent() {
        if (parent == null) {
            return null;
        }

        return parent.get();
    }

    public void reinitialize(final TState state) {
        parent = null;
        owner = new WeakReference<>(this);
        action = state.getLastAction();
        setState(state);
    }

    public List<SearchNode<TAction, TEdge, TState>> createAllPossibleChildNodes(final EdgeFactory<TEdge> edgeFactory) {
        Iterable<TAction> possibleActions = state.createAllPossibleActions();

        return StreamSupport.stream(possibleActions.spliterator(), false)
                .map(action -> new SearchNode<>(this, action, edgeFactory.create()))
                .collect(Collectors.toList());
    }

    public void initializeState() {
        TState state = owner.get().state.accept(action);

        setState(state);
    }

    public boolean isExpanded() {
        return unexploredChildren != null;
    }

    public boolean isFullyExplored() {
        return isExpanded() && unexploredChildren.isEmpty() && explorableChildren.isEmpty();
    }

    @Override
    public String toString() {
        if (state == null) {
            return String.format("depth: UNKNOWN, actionId: %d, statusId: UNKNOWN", action.getId());
        }

        return String.format("depth: %d, actionId: %d, statusId: %d", state.getDepth(), action.getId(), state.getStatusId());
    }
}
