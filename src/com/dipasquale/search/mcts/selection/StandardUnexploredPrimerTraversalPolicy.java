package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardUnexploredPrimerTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractUnexploredPrimerTraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    private static final StandardUnexploredPrimerTraversalPolicy<?, ?, ?> INSTANCE = new StandardUnexploredPrimerTraversalPolicy<>();

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> StandardUnexploredPrimerTraversalPolicy<TAction, TEdge, TState> getInstance() {
        return (StandardUnexploredPrimerTraversalPolicy<TAction, TEdge, TState>) INSTANCE;
    }
}
