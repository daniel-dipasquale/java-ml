package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinimumFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -58721796874663202L;
    private static final MinimumFitnessDeterminerFactory INSTANCE = new MinimumFitnessDeterminerFactory();

    public static MinimumFitnessDeterminerFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public FitnessDeterminer create() {
        return new MinimumFitnessDeterminer();
    }
}
