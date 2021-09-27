package com.dipasquale.ai.common.fitness;

import java.io.Serial;
import java.io.Serializable;

public final class AverageFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -3798311517368633267L;

    @Override
    public FitnessDeterminer create() {
        return new AverageFitnessDeterminer();
    }
}
