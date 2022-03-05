package com.dipasquale.search.mcts;

@FunctionalInterface
public interface ActionEfficiencyCalculator<TAction extends Action, TEdge extends Edge> {
    float calculate(int depth, TAction action, TEdge edge);
}
