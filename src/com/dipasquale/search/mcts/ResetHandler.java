package com.dipasquale.search.mcts;

import java.util.List;

@FunctionalInterface
public interface ResetHandler {
    void reset();

    static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> List<ResetHandler> create(final Provider<TAction, TEdge, TState> provider) {
        if (!provider.isAllowedToCollect()) {
            return List.of();
        }

        return List.of(provider::clear);
    }
}
