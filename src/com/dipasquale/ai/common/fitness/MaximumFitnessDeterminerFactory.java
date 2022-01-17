package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaximumFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -8213265993365433024L;
    private static final MaximumFitnessDeterminerFactory INSTANCE = new MaximumFitnessDeterminerFactory();

    public static MaximumFitnessDeterminerFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public FitnessDeterminer create() {
        return new MaximumFitnessDeterminer();
    }
}
