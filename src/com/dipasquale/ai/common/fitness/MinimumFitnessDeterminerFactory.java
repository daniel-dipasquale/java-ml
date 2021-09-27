package com.dipasquale.ai.common.fitness;

import java.io.Serial;
import java.io.Serializable;

public final class MinimumFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -58721796874663202L;

    @Override
    public FitnessDeterminer create() {
        return new MinimumFitnessDeterminer();
    }
}
