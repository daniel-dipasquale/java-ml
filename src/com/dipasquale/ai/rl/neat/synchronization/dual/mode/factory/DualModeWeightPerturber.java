package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.common.factory.WeightPerturber;
import com.dipasquale.common.LimitSupport;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeWeightPerturber<T extends FloatFactory & DualModeObject> implements WeightPerturber, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -1787775418318205965L;
    private final T floatFactory;

    @Override
    public float perturb(final float value) {
        float perturbed = floatFactory.create() * value;

        return LimitSupport.getFiniteValue(perturbed);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        floatFactory.activateMode(concurrencyLevel);
    }
}
