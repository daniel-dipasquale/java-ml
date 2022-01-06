package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBackPropagationPolicy<TState extends SearchState, TEdge extends SearchEdge, TEnvironment extends Environment<TState, TEnvironment>> implements BackPropagationPolicy<TState, TEdge, TEnvironment> {
    private final BackPropagationObserver<TState, TEdge, TEnvironment> observer;

    protected abstract void processCurrent(SearchNode<TState, TEdge, TEnvironment> leafNode, int simulationStatusId, SearchNode<TState, TEdge, TEnvironment> currentNode);

    @Override
    public final void process(final SearchNode<TState, TEdge, TEnvironment> leafNode, final int simulationStatusId) {
        boolean isFullyExplored = true;

        for (SearchNode<TState, TEdge, TEnvironment> currentNode = leafNode; currentNode != null; ) {
            SearchNode<TState, TEdge, TEnvironment> parentNode = currentNode.getParent();

            processCurrent(leafNode, simulationStatusId, currentNode);

            if (parentNode != null) {
                if (isFullyExplored) {
                    parentNode.getExplorableChildren().remove(parentNode.getChildSelectedIndex());
                    parentNode.getFullyExploredChildren().add(currentNode);
                    isFullyExplored = parentNode.isFullyExplored();
                }

                parentNode.setChildSelectedIndex(-1);
            }

            currentNode = parentNode;
        }

        if (observer != null) {
            observer.notify(leafNode, simulationStatusId);
        }
    }
}
