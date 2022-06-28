package com.dipasquale.search.mcts.selection;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSelectionPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> implements SelectionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> unexploredPrimerTraversalPolicy;
    private final TraversalPolicy<TAction, TEdge, TState, TSearchNode> explorableTraversalPolicy;

    protected abstract TContext createContext();

    protected abstract void visit(TContext context, TSearchNode searchNode);

    protected abstract boolean shouldSelectCandidateLeaf(final TSearchNode candidateSearchNode);

    protected abstract boolean shouldSelectKnownLeaf(TSearchNode knownSearchNode);

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

            if (simulations == 1) {
                expansionPolicy.expand(nextSearchNode);
            }

            while (true) {
                TSearchNode candidateSearchNode = unexploredPrimerTraversalPolicy.next(simulations, nextSearchNode);

                if (candidateSearchNode != null) {
                    visit(context, candidateSearchNode);

                    if (shouldSelectCandidateLeaf(candidateSearchNode)) {
                        assert candidateSearchNode.isExpanded();

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
                    assert nextSearchNode.isExpanded();

                    if (!shouldSelectKnownLeaf(nextSearchNode)) {
                        return null;
                    }

                    return nextSearchNode;
                }
            }
        } finally {
            cleanUp(context);
        }
    }
}
