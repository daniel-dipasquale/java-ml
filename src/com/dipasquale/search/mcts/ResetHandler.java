package com.dipasquale.search.mcts;

import java.util.List;

@FunctionalInterface
public interface ResetHandler {
    void reset();

    static List<ResetHandler> create(final Buffer<?, ?, ?, ?> buffer) {
        return List.of(buffer::clear);
    }
}
