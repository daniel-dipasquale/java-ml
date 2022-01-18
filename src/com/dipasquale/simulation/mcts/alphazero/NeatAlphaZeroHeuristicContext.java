package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class NeatAlphaZeroHeuristicContext<TAction extends Action, TEnvironment extends State<TAction, TEnvironment>> {
    private final TEnvironment environment;
    private final int childrenCount;
}
