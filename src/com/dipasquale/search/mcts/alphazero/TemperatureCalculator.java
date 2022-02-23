package com.dipasquale.search.mcts.alphazero;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class TemperatureCalculator {
    @Builder.Default
    private final float initialValue = 0.001f;
    @Builder.Default
    private final float finalValue = 1f;
    @Builder.Default
    private final int simulationThreshold = 10;

    public float calculate(final int simulations, final float value) {
        if (simulations < simulationThreshold) {
            return (float) Math.pow(value, 1f / initialValue);
        }

        return (float) Math.pow(value, 1f / finalValue);
    }
}
