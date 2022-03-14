package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class BackPropagationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TContext> {
    private final BackPropagationStep<TAction, TEdge, TState, TContext> step;
    private final SimulationResultObserver<TAction, TEdge, TState> observer;

    public void process(final SearchNode<TAction, TEdge, TState> leafNode) {
        TContext context = step.createContext(leafNode);
        boolean isFullyExplored = leafNode.isFullyExplored() || leafNode.getState().getStatusId() != MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;

        for (SearchNode<TAction, TEdge, TState> currentNode = leafNode; currentNode != null; ) {
            SearchNode<TAction, TEdge, TState> parentNode = currentNode.getParent();

            step.process(context, currentNode);

            if (parentNode != null) {
                if (isFullyExplored) {
                    parentNode.getExplorableChildren().remove(parentNode.getSelectedExplorableChildIndex());
                    parentNode.getFullyExploredChildren().add(currentNode);
                    isFullyExplored = parentNode.isFullyExplored();
                }

                parentNode.setSelectedExplorableChildIndex(SearchNode.NO_CHILD_SELECTED_INDEX);
            }

            currentNode = parentNode;
        }

        if (observer != null) {
            observer.notify(leafNode);
        }
    }
}
