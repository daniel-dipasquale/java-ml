package com.dipasquale.ai.common.fitness;

import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
public final class LastValueFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 422605264854317067L;

    @Override
    public FitnessDeterminer create() {
        return new LastValueFitnessDeterminer();
    }
}
