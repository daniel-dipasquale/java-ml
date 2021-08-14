package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class LastValueFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 422605264854317067L;

    @Override
    public FitnessDeterminer create() {
        return new LastValueFitnessDeterminer();
    }
}
