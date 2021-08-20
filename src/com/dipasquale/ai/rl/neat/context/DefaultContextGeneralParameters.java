package com.dipasquale.ai.rl.neat.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class DefaultContextGeneralParameters implements Context.GeneralParams, Serializable {
    @Serial
    private static final long serialVersionUID = 7845128541290517542L;
    private final int populationSize;

    @Override
    public int populationSize() {
        return populationSize;
    }
}
