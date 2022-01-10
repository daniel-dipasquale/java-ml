package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class NeatAlphaZeroHeuristicContext<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> {
    private final TEnvironment environment;
    private final int childrenCount;
}
