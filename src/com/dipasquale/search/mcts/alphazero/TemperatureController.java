package com.dipasquale.search.mcts.alphazero;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class TemperatureController {
    @Builder.Default
    private final float initialValue = 0.001f;
    @Builder.Default
    private final float finalValue = 1f;
    @Builder.Default
    private final int depthThreshold = 2;

    public float getTemperature(final int depth, final float value) {
        if (depth <= depthThreshold) {
            return (float) Math.pow(value, 1f / initialValue);
        }

        return (float) Math.pow(value, 1f / finalValue);
    }
}
