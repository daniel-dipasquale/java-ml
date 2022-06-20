package com.dipasquale.search.mcts.propagation;

import com.dipasquale.data.structure.iterator.LinkedIterator;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeExplorer;
import com.dipasquale.search.mcts.SearchNodeResult;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBackPropagationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> implements BackPropagationPolicy<TAction, TEdge, TState, TSearchNode> {
    private final SearchNodeExplorer<TAction, TEdge, TState, TSearchNode> searchNodeExplorer;
    private final BackPropagationStep<TAction, TEdge, TState, TSearchNode, TContext> step;
    private final BackPropagationObserver<TAction, TState> observer;

    @Override
    public void process(final TSearchNode rootSearchNode, final TSearchNode selectedSearchNode, final TSearchNode leafSearchNode) {
        boolean isLeafFullyExplored = searchNodeExplorer.isFullyExplored(leafSearchNode);
        int leafStatusId = leafSearchNode.getState().getStatusId();
        boolean isLeafInProgress = leafStatusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;

        if (!isLeafFullyExplored || !isLeafInProgress) { // TODO: keep track how many times this statement is false
            TContext context = step.createContext(leafSearchNode);
            boolean isFullyExplored = !isLeafInProgress;

            for (TSearchNode currentSearchNode = leafSearchNode; currentSearchNode != null; ) {
                TSearchNode parentSearchNode = currentSearchNode.getParent();

                step.process(context, currentSearchNode);

                if (parentSearchNode != null) {
                    if (isFullyExplored) {
                        isFullyExplored = searchNodeExplorer.declareFullyExplored(currentSearchNode);
                    }

                    parentSearchNode.setSelectedExplorableChildKey(SearchNode.NO_SELECTED_EXPLORABLE_CHILD_KEY);
                }

                currentSearchNode = parentSearchNode;
            }

            if (observer != null) {
                Iterable<SearchNodeResult<TAction, TState>> results = LinkedIterator.createStream(leafSearchNode, SearchNode::getParent)
                        .map(SearchNode::getResult)
                        ::iterator;

                observer.notify(leafStatusId, results);
            }
        }
    }
}
