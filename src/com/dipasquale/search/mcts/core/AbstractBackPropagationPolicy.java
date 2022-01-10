package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBackPropagationPolicy<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> implements BackPropagationPolicy<TState, TEdge, TEnvironment> {
    private final BackPropagationObserver<TState, TEdge, TEnvironment> observer;

    protected abstract void process(SearchNode<TState, TEdge, TEnvironment> leafNode, int simulationStatusId, SearchNode<TState, TEdge, TEnvironment> currentNode);

    @Override
    public final void process(final SearchNode<TState, TEdge, TEnvironment> leafNode, final int simulationStatusId) {
        boolean isFullyExplored = true;

        for (SearchNode<TState, TEdge, TEnvironment> currentNode = leafNode; currentNode != null; ) {
            SearchNode<TState, TEdge, TEnvironment> parentNode = currentNode.getParent();

            process(leafNode, simulationStatusId, currentNode);

            if (parentNode != null) {
                if (isFullyExplored) {
                    parentNode.getExplorableChildren().remove(parentNode.getSelectedExplorableChildIndex());
                    parentNode.getFullyExploredChildren().add(currentNode);
                    isFullyExplored = parentNode.isFullyExplored();
                }

                parentNode.setSelectedExplorableChildIndex(-1);
            }

            currentNode = parentNode;
        }

        if (observer != null) {
            observer.notify(leafNode, simulationStatusId);
        }
    }
}
