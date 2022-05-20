package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.iterator.LinkedIterator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class BackPropagationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> {
    private final BackPropagationStep<TAction, TEdge, TState, TSearchNode, TContext> step;
    private final BackPropagationObserver<TAction, TState> observer;

    public void process(final TSearchNode leafSearchNode) {
        TContext context = step.createContext(leafSearchNode);
        boolean isFullyExplored = leafSearchNode.isFullyExplored() || leafSearchNode.getState().getStatusId() != MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;

        for (TSearchNode currentSearchNode = leafSearchNode; currentSearchNode != null; ) {
            TSearchNode parentSearchNode = currentSearchNode.getParent();

            step.process(context, currentSearchNode);

            if (parentSearchNode != null) {
                if (isFullyExplored) {
                    if (parentSearchNode.getExplorableChildren().removeByKey(parentSearchNode.getSelectedExplorableChildKey()) != null) {
                        parentSearchNode.getFullyExploredChildren().add(currentSearchNode);
                    }

                    isFullyExplored = parentSearchNode.isFullyExplored();
                }

                parentSearchNode.setSelectedExplorableChildKey(SearchNode.NO_SELECTED_EXPLORABLE_CHILD_KEY);
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
