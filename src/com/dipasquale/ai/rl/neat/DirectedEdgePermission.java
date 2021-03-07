package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class DirectedEdgePermission {
    @Getter
    private final DirectedEdge directedEdge;
    private int cyclesAllowed;

    public boolean isCycleAllowed() {
        if (cyclesAllowed > 0) {
            cyclesAllowed--;

            return true;
        }

        return false;
    }
}
