package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.StateId;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor
public final class GenerationTree<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    private Generation<TAction, TEdge, TState, TSearchNode> generation = new Generation<>();

    public void seed(final TSearchNode searchNode) {
        generation.replace(searchNode);
    }

    private static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> Generation<TAction, TEdge, TState, TSearchNode> queryDescendantGeneration(final Iterator<StateId> tokenizedTreeIds, final Generation<TAction, TEdge, TState, TSearchNode> firstGeneration) {
        Generation<TAction, TEdge, TState, TSearchNode> temporaryGeneration = firstGeneration;

        if (tokenizedTreeIds.hasNext()) {
            do {
                temporaryGeneration = temporaryGeneration.getDescendant(tokenizedTreeIds.next());
            } while (tokenizedTreeIds.hasNext() && temporaryGeneration != null);

            if (tokenizedTreeIds.hasNext()) {
                return null;
            }
        }

        return temporaryGeneration;
    }

    private Generation<TAction, TEdge, TState, TSearchNode> queryDescendantGeneration(final StateId stateId) {
        StateId ancestorStateId = generation.getSearchNode().getStateId();
        Iterable<StateId> tokenizedTreeIdIs = stateId.tokenizeUntil(ancestorStateId);

        return queryDescendantGeneration(tokenizedTreeIdIs.iterator(), generation);
    }

    public TSearchNode reseed(final StateId stateId) {
        if (generation.isInitialized()) {
            Generation<TAction, TEdge, TState, TSearchNode> descendantGeneration = queryDescendantGeneration(stateId);

            if (descendantGeneration != null) {
                generation = descendantGeneration;

                return generation.getSearchNode();
            }
        }

        return null;
    }

    public void branchOut(final TSearchNode searchNode) {
        Generation<TAction, TEdge, TState, TSearchNode> descendantGeneration = queryDescendantGeneration(searchNode.getStateId());

        descendantGeneration.allocateDescendants(searchNode);
    }

    public void clear() {
        generation.clear();
    }
}
