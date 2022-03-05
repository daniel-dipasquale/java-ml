package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public final class SearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private static final int NO_CHILD_SELECTED_INDEX = -1;
    private static final boolean IS_SIMULATION = true;
    private SearchNode<TAction, TEdge, TState> parent;
    @Getter(AccessLevel.NONE)
    private final SearchNode<TAction, TEdge, TState> owner;
    private final TAction action;
    private final TEdge edge;
    private final int depth;
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

    SearchNode(final TEdge edge, final TState state, final int depth) {
        this.parent = null;
        this.owner = this;
        this.action = state.getLastAction();
        this.edge = edge;
        this.depth = depth;
        this.state = state;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    private SearchNode(final SearchNode<TAction, TEdge, TState> parent, final TAction action, final TEdge edge) {
        this.parent = parent;
        this.owner = parent;
        this.action = action;
        this.edge = edge;
        this.depth = parent.depth + 1;
        this.state = null;
        this.unexploredChildren = null;
        this.explorableChildren = null;
        this.fullyExploredChildren = null;
        this.selectedExplorableChildIndex = NO_CHILD_SELECTED_INDEX;
    }

    void removeParent() {
        parent = null; // NOTE: the parent from the edge isn't removed, at the moment, this does not matter
    }

    public List<SearchNode<TAction, TEdge, TState>> createAllPossibleChildNodes(final EdgeFactory<TEdge> edgeFactory) {
        Iterable<TAction> possibleActions = state.createAllPossibleActions();

        return StreamSupport.stream(possibleActions.spliterator(), false)
                .map(action -> new SearchNode<>(this, action, edgeFactory.create(edge)))
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
        return isExpanded() && unexploredChildren.isEmpty() && explorableChildren.isEmpty() || state.getStatusId() != MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }
}
