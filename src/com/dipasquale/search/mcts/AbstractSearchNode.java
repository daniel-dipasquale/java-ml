package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Getter
public abstract class AbstractSearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends AbstractSearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchNode<TAction, TEdge, TState, TSearchNode> {
    @Getter(AccessLevel.NONE)
    private WeakReference<AbstractSearchNode<TAction, TEdge, TState, TSearchNode>> parent;
    @Getter(AccessLevel.NONE)
    private WeakReference<AbstractSearchNode<TAction, TEdge, TState, TSearchNode>> stateOwner;
    @Setter(AccessLevel.PRIVATE)
    private SearchNodeResult<TAction, TState> result;
    private final TEdge edge;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> fullyExploredChildren;

    private AbstractSearchNode(final TSearchNode parent, final TSearchNode stateOwner, final SearchNodeResult<TAction, TState> result, final TEdge edge) {
        this.parent = new WeakReference<>(parent);
        this.stateOwner = new WeakReference<>(Objects.requireNonNullElse(stateOwner, this));
        this.result = result;
        this.edge = edge;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
    }

    protected AbstractSearchNode(final SearchNodeResult<TAction, TState> result, final TEdge edge) {
        this(null, null, result, edge);
    }

    protected AbstractSearchNode(final TSearchNode parent, final SearchNodeResult<TAction, TState> result, final TEdge edge) {
        this(parent, parent, result, edge);
    }

    @Override
    public TSearchNode getParent() {
        if (parent == null) {
            return null;
        }

        return (TSearchNode) parent.get();
    }

    @Override
    public TAction getAction() {
        return result.getAction();
    }

    @Override
    public TState getState() {
        return result.getState();
    }

    @Override
    public StateId getStateId() {
        return result.getStateId();
    }

    @Override
    public void reinitialize(final SearchNodeResult<TAction, TState> result) {
        parent = null;
        stateOwner = new WeakReference<>(this);
        setResult(result);
    }

    protected abstract TSearchNode createChild(SearchNodeResult<TAction, TState> result, EdgeFactory<TEdge> edgeFactory);

    private TSearchNode createChild(final TAction action, final EdgeFactory<TEdge> edgeFactory) {
        SearchNodeResult<TAction, TState> childResult = getResult().createChild(action);

        return createChild(childResult, edgeFactory);
    }

    @Override
    public Iterable<TSearchNode> createAllPossibleChildren(final EdgeFactory<TEdge> edgeFactory) {
        Iterable<TAction> possibleActions = getState().createAllPossibleActions();

        return StreamSupport.stream(possibleActions.spliterator(), false)
                .map(action -> createChild(action, edgeFactory))
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
        TAction action = getAction();
        TState state = getState();
        StateId stateId = getStateId();

        if (state == null) {
            return String.format("depth: %d, actionId: %d, statusId: UNKNOWN", stateId.getDepth(), action.getId());
        }

        return String.format("depth: %d, actionId: %d, statusId: %d", stateId.getDepth(), action.getId(), state.getStatusId());
    }
}
