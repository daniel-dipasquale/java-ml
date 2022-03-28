package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.iterator.LinkedIterator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class BackPropagationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TContext> {
    private final BackPropagationStep<TAction, TEdge, TState, TContext> step;
    private final BackPropagationObserver<TAction, TState> observer;

    public void process(final SearchNode<TAction, TEdge, TState> leafSearchNode) {
        TContext context = step.createContext(leafSearchNode);
        boolean isFullyExplored = leafSearchNode.isFullyExplored() || leafSearchNode.getState().getStatusId() != MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;

        for (SearchNode<TAction, TEdge, TState> currentSearchNode = leafSearchNode; currentSearchNode != null; ) {
            SearchNode<TAction, TEdge, TState> parentSearchNode = currentSearchNode.getParent();

            step.process(context, currentSearchNode);

            if (parentSearchNode != null) {
                if (isFullyExplored) {
                    parentSearchNode.getExplorableChildren().remove(parentSearchNode.getSelectedExplorableChildIndex());
                    parentSearchNode.getFullyExploredChildren().add(currentSearchNode);
                    isFullyExplored = parentSearchNode.isFullyExplored();
                }

                parentSearchNode.setSelectedExplorableChildIndex(SearchNode.NO_CHILD_SELECTED_INDEX);
            }

            currentSearchNode = parentSearchNode;
        }

        if (observer != null) {
            Iterable<TState> states = LinkedIterator.createStream(leafSearchNode, SearchNode::getParent)
                    .map(SearchNode::getState)
                    ::iterator;

            observer.notify(leafSearchNode.getState().getStatusId(), states);
        }
    }
}
