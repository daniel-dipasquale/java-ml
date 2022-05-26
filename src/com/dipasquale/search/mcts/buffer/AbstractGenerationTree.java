package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Iterator;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractGenerationTree<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TGeneration extends Generation<TAction, TEdge, TState, TSearchNode, TGeneration>> implements GenerationTree<TAction, TEdge, TState, TSearchNode> {
    private TGeneration generation;

    private TGeneration queryOrBuildGeneration(final TreeId treeId, final boolean shouldBuild) {
        TreeId rootTreeId = generation.getTreeId();
        Iterable<TreeId> tokenizedTreeIdIterable = treeId.tokenizeFrom(rootTreeId);

        if (tokenizedTreeIdIterable == null) {
            return null;
        }

        Iterator<TreeId> tokenizedTreeIds = tokenizedTreeIdIterable.iterator();
        TGeneration fixedGeneration = generation;

        if (tokenizedTreeIds.hasNext()) {
            if (shouldBuild) {
                do {
                    fixedGeneration = fixedGeneration.getOrCreateNext(tokenizedTreeIds.next());
                } while (tokenizedTreeIds.hasNext());
            } else {
                do {
                    fixedGeneration = fixedGeneration.getNext(tokenizedTreeIds.next());
                } while (tokenizedTreeIds.hasNext() && fixedGeneration != null);
            }
        }

        return fixedGeneration;
    }

    protected static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TGeneration extends Generation<TAction, TEdge, TState, TSearchNode, TGeneration>> void replaceAllChildren(final TGeneration generation, final TSearchNode searchNode) {
        generation.replace(searchNode);

        if (searchNode.isExpanded()) {
            for (TSearchNode childSearchNode : searchNode.getUnexploredChildren()) {
                generation.getOrCreateNext(childSearchNode.getAction().getTreeId()).replace(childSearchNode);
            }

            for (TSearchNode childSearchNode : searchNode.getExplorableChildren()) {
                generation.getOrCreateNext(childSearchNode.getAction().getTreeId()).replace(childSearchNode);
            }

            for (TSearchNode childSearchNode : searchNode.getFullyExploredChildren()) {
                generation.getOrCreateNext(childSearchNode.getAction().getTreeId()).replace(childSearchNode);
            }
        }
    }

    @Override
    public TSearchNode get(final TState state) {
        if (generation.isInitialized()) {
            TreeId treeId = state.getLastAction().getTreeId();
            TGeneration nextGeneration = queryOrBuildGeneration(treeId, false);

            if (nextGeneration != null) {
                generation = nextGeneration;

                return generation.getSearchNode();
            }
        }

        return null;
    }

    @Override
    public void put(final TSearchNode searchNode) {
        if (!generation.isInitialized()) {
            generation.replace(searchNode);
        }

        TGeneration nextGeneration = queryOrBuildGeneration(searchNode.getAction().getTreeId(), true);

        if (nextGeneration != null) {
            replaceAllChildren(nextGeneration, searchNode);
        }
    }

    @Override
    public void clear() {
        generation.clear();
    }
}
