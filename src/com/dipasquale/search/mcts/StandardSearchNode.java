package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public final class StandardSearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNode<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    static final int NO_CHILD_SELECTED_INDEX = -1;
    @Getter(AccessLevel.NONE)
    private WeakReference<StandardSearchNode<TAction, TEdge, TState>> parent;
    @Getter(AccessLevel.NONE)
    private WeakReference<StandardSearchNode<TAction, TEdge, TState>> stateOwner;
    private TAction action;
    private final TEdge edge;
    @Setter(AccessLevel.PRIVATE)
    private TState state;
    @Setter
    private List<StandardSearchNode<TAction, TEdge, TState>> unexploredChildren;
    @Setter
    private List<StandardSearchNode<TAction, TEdge, TState>> explorableChildren;
    @Setter
    private List<StandardSearchNode<TAction, TEdge, TState>> fullyExploredChildren;
    @Setter
    private int selectedExplorableChildIndex;

    private StandardSearchNode(final TEdge edge, final TState state) {
        this.parent = new WeakReference<>(null);
        this.stateOwner = new WeakReference<>(this);
        this.action = state.getLastAction();
        this.edge = edge;
        this.state = state;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    private StandardSearchNode(final StandardSearchNode<TAction, TEdge, TState> parent, final TAction action, final TEdge edge) {
        this.parent = new WeakReference<>(parent);
        this.stateOwner = new WeakReference<>(parent);
        this.action = action;
        this.edge = edge;
        this.state = null;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> StandardSearchNode<TAction, TEdge, TState> createRoot(final EdgeFactory<TEdge> edgeFactory, final TState state) {
        return new StandardSearchNode<>(edgeFactory.create(), state);
    }

    @Override
    public StandardSearchNode<TAction, TEdge, TState> getParent() {
        if (parent == null) {
            return null;
        }

        return parent.get();
    }

    @Override
    public void reinitialize(final TState state) {
        parent = null;
        stateOwner = new WeakReference<>(this);
        action = state.getLastAction();
        setState(state);
    }

    @Override
    public List<StandardSearchNode<TAction, TEdge, TState>> createAllPossibleChildNodes(final EdgeFactory<TEdge> edgeFactory) {
        Iterable<TAction> possibleActions = state.createAllPossibleActions();

        return StreamSupport.stream(possibleActions.spliterator(), false)
                .map(action -> new StandardSearchNode<>(this, action, edgeFactory.create()))
                .collect(Collectors.toList());
    }

    @Override
    public void initializeState() {
        TState state = stateOwner.get().state.accept(action);

        setState(state);
    }

    @Override
    public boolean isExpanded() {
        return unexploredChildren != null;
    }

    @Override
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
