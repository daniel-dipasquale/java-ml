package com.dipasquale.ai.common;

import com.dipasquale.common.FloatFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class WeightPerturberDefault implements WeightPerturber {
    @Serial
    private static final long serialVersionUID = 3538961269200792337L;
    private final FloatFactory factory;

    @Override
    public float perturb(final float value) {
        return factory.create() * value;
    }
}
