package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSelectionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> implements SelectionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> priorityTraversalPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> subsequentTraversalPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    protected abstract TContext createContext();

    protected abstract void visit(TContext context, TSearchNode searchNode);

    protected boolean expand(final TSearchNode searchNode) {
        assert !searchNode.isExpanded();

        expansionPolicy.expand(searchNode);

        return searchNode.getState().isIntentional();
    }

    protected abstract void exit(TContext context);

    @Override
    public TSearchNode select(final int simulations, final TSearchNode rootSearchNode) {
        TContext context = createContext();

        try {
            TSearchNode nextSearchNode = rootSearchNode;

            visit(context, nextSearchNode);

            TSearchNode candidateSearchNode = priorityTraversalPolicy.next(simulations, nextSearchNode);

            while (true) {
                if (candidateSearchNode != null) {
                    visit(context, candidateSearchNode);

                    if (expand(candidateSearchNode)) {
                        return candidateSearchNode;
                    }

                    nextSearchNode = subsequentTraversalPolicy.next(simulations, candidateSearchNode);
                } else {
                    nextSearchNode = subsequentTraversalPolicy.next(simulations, nextSearchNode);
                }

                if (nextSearchNode == null) {
                    return null;
                }

                visit(context, nextSearchNode);

                if (nextSearchNode.isExpanded()) {
                    candidateSearchNode = priorityTraversalPolicy.next(simulations, nextSearchNode);
                } else {
                    candidateSearchNode = nextSearchNode;
                }
            }
        } finally {
            exit(context);
        }
    }
}
