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
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> unexploredPrimerTraversalPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> explorableTraversalPolicy;
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    protected abstract TContext createContext();

    protected abstract void visit(TContext context, TSearchNode searchNode);

    protected boolean shouldSelect(final TSearchNode searchNode) {
        expansionPolicy.expand(searchNode);

        return true;
    }

    protected TSearchNode selectLeaf(final TSearchNode searchNode) {
        return searchNode;
    }

    protected TSearchNode selectNone() {
        return null;
    }

    protected abstract void cleanUp(TContext context);

    @Override
    public TSearchNode select(final int simulations, final TSearchNode rootSearchNode) {
        TContext context = createContext();
        TSearchNode nextSearchNode = rootSearchNode;

        try {
            visit(context, nextSearchNode);

            while (true) {
                TSearchNode candidateSearchNode = unexploredPrimerTraversalPolicy.next(simulations, nextSearchNode);

                if (candidateSearchNode != null) {
                    visit(context, candidateSearchNode);

                    if (shouldSelect(candidateSearchNode)) {
                        return candidateSearchNode;
                    }

                    nextSearchNode = explorableTraversalPolicy.next(simulations, candidateSearchNode);
                } else {
                    nextSearchNode = explorableTraversalPolicy.next(simulations, nextSearchNode);
                }

                if (nextSearchNode == null) {
                    return selectNone();
                }

                visit(context, nextSearchNode);

                if (nextSearchNode.isFullyExplored()) {
                    return selectLeaf(nextSearchNode);
                }
            }
        } finally {
            cleanUp(context);
        }
    }
}
