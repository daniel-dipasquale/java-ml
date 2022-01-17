package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SumFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 2866239540588502526L;
    private static final SumFitnessDeterminerFactory INSTANCE = new SumFitnessDeterminerFactory();

    public static SumFitnessDeterminerFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public FitnessDeterminer create() {
        return new SumFitnessDeterminer();
    }
}
