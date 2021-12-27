package com.dipasquale.ai.common.fitness;

import java.io.Serial;
import java.io.Serializable;

public final class SumFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 2866239540588502526L;

    @Override
    public FitnessDeterminer create() {
        return new SumFitnessDeterminer();
    }
}
