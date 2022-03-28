package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class SearchNodeTree<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private Generation<TAction, TEdge, TState> generation = new Generation<>();

    private static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> Generation<TAction, TEdge, TState> getOrCreate(final Generation<TAction, TEdge, TState> generation, final TreeId treeId) {
        return generation.descendents.computeIfAbsent(treeId, __ -> new Generation<>());
    }

    private Generation<TAction, TEdge, TState> queryGeneration(final TreeId treeId, final boolean createPath) {
        TreeId rootTreeId = generation.searchNode.getAction().getTreeId();
        Iterable<TreeId> tokenizedCacheIdIterable = treeId.tokenizeFrom(rootTreeId);

        if (tokenizedCacheIdIterable == null) {
            return null;
        }

        Iterator<TreeId> tokenizedCacheIds = tokenizedCacheIdIterable.iterator();
        Generation<TAction, TEdge, TState> fixedGeneration = generation;

        if (tokenizedCacheIds.hasNext()) {
            if (createPath) {
                do {
                    fixedGeneration = getOrCreate(fixedGeneration, tokenizedCacheIds.next());
                } while (tokenizedCacheIds.hasNext());
            } else {
                do {
                    fixedGeneration = fixedGeneration.descendents.get(tokenizedCacheIds.next());
                } while (tokenizedCacheIds.hasNext() && fixedGeneration != null);
            }
        }

        return fixedGeneration;
    }

    private static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> void replace(final Generation<TAction, TEdge, TState> generation, final SearchNode<TAction, TEdge, TState> searchNode) {
        generation.searchNode = searchNode;
        generation.parentTreeId = searchNode.getAction().getTreeId().getParent();
    }

    public void collect(final SearchNode<TAction, TEdge, TState> searchNode) {
        if (generation.searchNode == null) {
            replace(generation, searchNode);
        }

        Generation<TAction, TEdge, TState> nextGeneration = queryGeneration(searchNode.getAction().getTreeId(), true);

        if (nextGeneration != null) {
            replace(nextGeneration, searchNode);

            if (searchNode.isExpanded()) {
                for (SearchNode<TAction, TEdge, TState> childSearchNode : searchNode.getUnexploredChildren()) {
                    replace(getOrCreate(nextGeneration, childSearchNode.getAction().getTreeId()), childSearchNode);
                }

                for (SearchNode<TAction, TEdge, TState> childSearchNode : searchNode.getExplorableChildren()) {
                    replace(getOrCreate(nextGeneration, childSearchNode.getAction().getTreeId()), childSearchNode);
                }

                for (SearchNode<TAction, TEdge, TState> childSearchNode : searchNode.getFullyExploredChildren()) {
                    replace(getOrCreate(nextGeneration, childSearchNode.getAction().getTreeId()), childSearchNode);
                }
            }
        }
    }

    public SearchNode<TAction, TEdge, TState> recall(final TState state) {
        if (generation.searchNode != null) {
            TreeId treeId = state.getLastAction().getTreeId();
            Generation<TAction, TEdge, TState> nextGeneration = queryGeneration(treeId, false);

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
    private static final class Generation<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
        private SearchNode<TAction, TEdge, TState> searchNode = null;
        private TreeId parentTreeId = null;
        private final Map<TreeId, Generation<TAction, TEdge, TState>> descendents = new HashMap<>();
    }
}
