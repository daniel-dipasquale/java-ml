package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.ref.WeakReference;
import java.util.stream.StreamSupport;

@Getter
public abstract class AbstractSearchNode<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchNode<TAction, TEdge, TState, TSearchNode> {
    @Getter(AccessLevel.NONE)
    private WeakReference<TSearchNode> parent;
    private final SearchResult<TAction, TState> result;
    private final TEdge edge;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren;
    @Setter
    private SearchNodeGroup<TAction, TEdge, TState, TSearchNode> fullyExploredChildren;

    private static <TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> SearchNodeGroup<TAction, TEdge, TState, TSearchNode> provideChildren(final SearchResult<TAction, TState> result) {
        if (result.getState().getStatusId() == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
            return null;
        }

        return EmptySearchNodeGroup.getInstance();
    }

    protected AbstractSearchNode(final TSearchNode parent, final SearchResult<TAction, TState> result, final TEdge edge) {
        this.parent = new WeakReference<>(parent);
        this.result = result;
        this.edge = edge;
        this.unexploredChildren = provideChildren(result);
        this.explorableChildren = provideChildren(result);
        this.fullyExploredChildren = provideChildren(result);
    }

    @Override
    public TSearchNode getParent() {
        return parent.get();
    }

    @Override
    public void reinitialize(final SearchResult<TAction, TState> result) {
        parent = new WeakReference<>(null);
        getResult().reinitialize(result);
    }

    protected abstract TSearchNode createChild(SearchResult<TAction, TState> result, EdgeFactory<TEdge> edgeFactory);

    private TSearchNode createChild(final int actionId, final TAction action, final EdgeFactory<TEdge> edgeFactory) {
        SearchResult<TAction, TState> childResult = getResult().createChild(actionId, action);

        return createChild(childResult, edgeFactory);
    }

    @Override
    public Iterable<TSearchNode> createAllPossibleChildren(final EdgeFactory<TEdge> edgeFactory) {
        Iterable<TAction> possibleActions = getState().createAllPossibleActions();
        int[] actionId = new int[]{0};

        return StreamSupport.stream(possibleActions.spliterator(), false)
                .map(action -> createChild(actionId[0]++, action, edgeFactory))
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
        return String.format("depth: %d, actionId: %d, statusId: %d", getStateId().getDepth(), getActionId(), getState().getStatusId());
    }
}
