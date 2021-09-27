package com.dipasquale.ai.common.fitness;

import java.io.Serial;
import java.io.Serializable;

public final class MaximumFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -8213265993365433024L;

    @Override
    public FitnessDeterminer create() {
        return new MaximumFitnessDeterminer();
    }
}
