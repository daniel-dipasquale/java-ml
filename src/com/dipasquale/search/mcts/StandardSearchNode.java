package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public final class StandardSearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSearchNode<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    @Setter(AccessLevel.PROTECTED)
    private TState state;
    @Getter
    @Setter
    private int selectedExplorableChildKey;

    StandardSearchNode(final TEdge edge, final TState state) {
        super(edge, state.getLastAction());
        this.state = state;
        this.selectedExplorableChildKey = NO_SELECTED_EXPLORABLE_CHILD_KEY;
    }

    private StandardSearchNode(final StandardSearchNode<TAction, TEdge, TState> parent, final TAction action, final TEdge edge) {
        super(parent, action, edge);
        this.state = null;
        this.selectedExplorableChildKey = NO_SELECTED_EXPLORABLE_CHILD_KEY;
    }

    @Override
    public TState getState() {
        if (state == null) {
            state = createState();
        }

        return state;
    }

    @Override
    protected StandardSearchNode<TAction, TEdge, TState> createChildNode(final TAction action, final EdgeFactory<TEdge> edgeFactory) {
        return new StandardSearchNode<>(this, action, edgeFactory.create());
    }
}
