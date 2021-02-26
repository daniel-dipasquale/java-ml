package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class DirectedEdgePermission<T> {
    @Getter
    private final DirectedEdge<T> directedEdge;
    private int cyclesAllowed;

    public boolean isCycleAllowed() {
        if (cyclesAllowed > 0) {
            cyclesAllowed--;

            return true;
        }

        return false;
    }
}
