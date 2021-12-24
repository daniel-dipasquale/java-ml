package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MultiBackPropagationPolicy<T extends State> implements BackPropagationPolicy<T> {
    private final List<BackPropagationPolicy<T>> backPropagationPolicies;

    @Override
    public boolean process(final SearchNode<T> searchNode, final int statusId) {
        for (BackPropagationPolicy<T> backPropagationPolicy : backPropagationPolicies) {
            if (!backPropagationPolicy.process(searchNode, statusId)) {
                return false;
            }
        }

        return true;
    }
}
