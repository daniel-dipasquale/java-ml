package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
public interface RewardHeuristic<TAction extends Action, TState extends State<TAction, TState>> {
    float estimate(TState state);

    private static float calculateUnboundedInternal(final float value, final float control) {
        float positiveValue = Math.abs(value);

        return (positiveValue - (float) Math.pow(positiveValue, control)) / value;
    }

    static float calculateUnbounded(final float value, final float control) {
        int comparison = Float.compare(value, 0f);

        if (comparison == 0) {
            return 0f;
        }

        if (comparison < 0) {
            return calculateUnboundedInternal(value - 1f, control);
        }

        return calculateUnboundedInternal(value + 1f, control);
    }

    static float calculateUnbounded(final float value) {
        return calculateUnbounded(value, 0.85f);
    }

    static float convertProbability(final float value) {
        return value * 2f - 1f;
    }
}
