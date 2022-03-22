package com.dipasquale.search.mcts;

import java.util.List;

@FunctionalInterface
public interface ResetHandler {
    void reset();

    static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> List<ResetHandler> create(final Cache<TAction, TEdge, TState> cache) {
        if (cache == null) {
            return List.of();
        }

        return List.of(cache::clear);
    }
}
