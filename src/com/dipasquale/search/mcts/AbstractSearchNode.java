package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.ref.WeakReference;
import java.util.stream.StreamSupport;

@Getter
public abstract class AbstractSearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends AbstractSearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchNode<TAction, TEdge, TState, TSearchNode> {
    @Getter(AccessLevel.NONE)
    private WeakReference<AbstractSearchNode<TAction, TEdge, TState, TSearchNode>> parent;
    @Getter(AccessLevel.NONE)
    private WeakReference<AbstractSearchNode<TAction, TEdge, TState, TSearchNode>> stateOwner;
    private TAction action;
    private final TEdge edge;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> fullyExploredChildren;

    protected AbstractSearchNode(final TEdge edge, final TAction action) {
        this.parent = new WeakReference<>(null);
        this.stateOwner = new WeakReference<>(this);
        this.action = action;
        this.edge = edge;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
    }

    protected AbstractSearchNode(final TSearchNode parent, final TAction action, final TEdge edge) {
        this.parent = new WeakReference<>(parent);
        this.stateOwner = new WeakReference<>(parent);
        this.action = action;
        this.edge = edge;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
    }

    @Override
    public TSearchNode getParent() {
        if (parent == null) {
            return null;
        }

        return (TSearchNode) parent.get();
    }

    protected TState createState() {
        TState state = stateOwner.get().getState();

        return state.accept(action);
    }

    protected abstract void setState(TState newState);

    @Override
    public void reinitialize(final TState state) {
        parent = null;
        stateOwner = new WeakReference<>(this);
        action = state.getLastAction();
        setState(state);
    }

    protected abstract TSearchNode createChildNode(TAction action, EdgeFactory<TEdge> edgeFactory);

    @Override
    public Iterable<TSearchNode> createAllPossibleChildNodes(final EdgeFactory<TEdge> edgeFactory) {
        Iterable<TAction> possibleActions = getState().createAllPossibleActions();

        return StreamSupport.stream(possibleActions.spliterator(), false)
                .map(action -> createChildNode(action, edgeFactory))
                ::iterator;
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
        TState state = getState();

        if (state == null) {
            return String.format("depth: UNKNOWN, actionId: %d, statusId: UNKNOWN", action.getId());
        }

        return String.format("depth: %d, actionId: %d, statusId: %d", state.getDepth(), action.getId(), state.getStatusId());
    }
}
