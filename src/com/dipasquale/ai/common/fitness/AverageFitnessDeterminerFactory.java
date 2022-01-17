package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AverageFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -3798311517368633267L;
    private static final AverageFitnessDeterminerFactory INSTANCE = new AverageFitnessDeterminerFactory();

    public static AverageFitnessDeterminerFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public FitnessDeterminer create() {
        return new AverageFitnessDeterminer();
    }
}
