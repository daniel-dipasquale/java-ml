package com.dipasquale.simulation.openai.gym.client;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Getter
public final class StepResult {
    private final boolean done;
    private final double[] observation;
    private final double reward;
}
