package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LastValueFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 422605264854317067L;
    private static final LastValueFitnessDeterminerFactory INSTANCE = new LastValueFitnessDeterminerFactory();

    public static LastValueFitnessDeterminerFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public FitnessDeterminer create() {
        return new LastValueFitnessDeterminer();
    }
}
