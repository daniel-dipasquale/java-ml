package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.LimitSupport;
import com.dipasquale.common.factory.FloatFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class WeightPerturber implements Serializable {
    @Serial
    private static final long serialVersionUID = -1787775418318205965L;
    private final FloatFactory floatFactory;

    public float perturb(final float value) {
        float perturbed = floatFactory.create() * value;

        return LimitSupport.getFiniteValue(perturbed);
    }
}
