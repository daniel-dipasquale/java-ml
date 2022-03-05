package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBackPropagationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements BackPropagationPolicy<TAction, TEdge, TState> {
    private final LeafNodeObserver<TAction, TEdge, TState> leafNodeObserver;

    protected abstract void process(SearchNode<TAction, TEdge, TState> leafNode, int simulationStatusId, SearchNode<TAction, TEdge, TState> currentNode);

    @Override
    public final void process(final SearchNode<TAction, TEdge, TState> leafNode, final int simulationStatusId) {
        boolean isFullyExplored = leafNode.isFullyExplored();

        for (SearchNode<TAction, TEdge, TState> currentNode = leafNode; currentNode != null; ) {
            SearchNode<TAction, TEdge, TState> parentNode = currentNode.getParent();

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

        if (leafNodeObserver != null) {
            leafNodeObserver.notify(leafNode, simulationStatusId);
        }
    }
}
