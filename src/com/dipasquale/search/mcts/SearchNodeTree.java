package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;

final class SearchNodeTree<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    private final MapFactory mapFactory;
    private Generation generation;

    SearchNodeTree(final MapFactory mapFactory) {
        this.mapFactory = mapFactory;
        this.generation = new Generation();
    }

    private Generation getOrCreate(final Generation generation, final TreeId treeId) {
        return generation.descendents.computeIfAbsent(treeId, __ -> new Generation());
    }

    private Generation queryGeneration(final TreeId treeId, final boolean createPath) {
        TreeId rootTreeId = generation.searchNode.getAction().getTreeId();
        Iterable<TreeId> tokenizedTreeIdIterable = treeId.tokenizeFrom(rootTreeId);

        if (tokenizedTreeIdIterable == null) {
            return null;
        }

        Iterator<TreeId> tokenizedTreeIds = tokenizedTreeIdIterable.iterator();
        Generation fixedGeneration = generation;

        if (tokenizedTreeIds.hasNext()) {
            if (createPath) {
                do {
                    fixedGeneration = getOrCreate(fixedGeneration, tokenizedTreeIds.next());
                } while (tokenizedTreeIds.hasNext());
            } else {
                do {
                    fixedGeneration = fixedGeneration.descendents.get(tokenizedTreeIds.next());
                } while (tokenizedTreeIds.hasNext() && fixedGeneration != null);
            }
        }

        return fixedGeneration;
    }

    private void replace(final Generation generation, final TSearchNode searchNode) {
        generation.searchNode = searchNode;
        generation.parentTreeId = searchNode.getAction().getTreeId().getParent();
    }

    public void collect(final TSearchNode searchNode) {
        if (generation.searchNode == null) {
            replace(generation, searchNode);
        }

        Generation nextGeneration = queryGeneration(searchNode.getAction().getTreeId(), true);

        if (nextGeneration != null) {
            replace(nextGeneration, searchNode);

            if (searchNode.isExpanded()) { // TODO: need lock
                for (TSearchNode childSearchNode : searchNode.getUnexploredChildren()) {
                    replace(getOrCreate(nextGeneration, childSearchNode.getAction().getTreeId()), childSearchNode);
                }

                for (TSearchNode childSearchNode : searchNode.getExplorableChildren()) {
                    replace(getOrCreate(nextGeneration, childSearchNode.getAction().getTreeId()), childSearchNode);
                }

                for (TSearchNode childSearchNode : searchNode.getFullyExploredChildren()) {
                    replace(getOrCreate(nextGeneration, childSearchNode.getAction().getTreeId()), childSearchNode);
                }
            }
        }
    }

    public TSearchNode recall(final TState state) {
        if (generation.searchNode != null) {
            TreeId treeId = state.getLastAction().getTreeId();
            Generation nextGeneration = queryGeneration(treeId, false);

            if (nextGeneration != null) {
                generation = nextGeneration;

                return generation.searchNode;
            }
        }

        return null;
    }

    public void clear() {
        generation.searchNode = null;
        generation.parentTreeId = null;
        generation.descendents.clear();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class Generation {
        private TSearchNode searchNode = null;
        private TreeId parentTreeId = null; // NOTE: avoids garbage collection
        private final Map<TreeId, Generation> descendents = mapFactory.create();
    }
}
