package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AvoidDoubleDipBackPropagationPolicy<T extends State> implements BackPropagationPolicy<T> {
    @Override
    public boolean process(final SearchNode<T> searchNode, final int statusId) {
        return searchNode.getVisited() == 0;
    }
}
