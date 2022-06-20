package com.dipasquale.search.mcts;

import lombok.Getter;
import lombok.Setter;

public final class StandardSearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSearchNode<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    @Getter
    @Setter
    private int selectedExplorableChildKey;

    StandardSearchNode(final SearchNodeResult<TAction, TState> result, final TEdge edge) {
        super(result, edge);
        this.selectedExplorableChildKey = NO_SELECTED_EXPLORABLE_CHILD_KEY;
    }

    private StandardSearchNode(final StandardSearchNode<TAction, TEdge, TState> parent, final SearchNodeResult<TAction, TState> result, final TEdge edge) {
        super(parent, result, edge);
        this.selectedExplorableChildKey = NO_SELECTED_EXPLORABLE_CHILD_KEY;
    }

    @Override
    protected StandardSearchNode<TAction, TEdge, TState> createChild(final SearchNodeResult<TAction, TState> result, final EdgeFactory<TEdge> edgeFactory) {
        return new StandardSearchNode<>(this, result, edgeFactory.create());
    }
}
