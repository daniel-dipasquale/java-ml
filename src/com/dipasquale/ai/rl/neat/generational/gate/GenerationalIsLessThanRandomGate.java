package com.dipasquale.ai.rl.neat.generational.gate;

import com.dipasquale.ai.rl.neat.generational.factory.GenerationalFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.gate.Gate;
import com.dipasquale.common.random.RandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class GenerationalIsLessThanRandomGate implements GenerationalFactory, Gate, Serializable {
    @Serial
    private static final long serialVersionUID = -7505532197100155133L;
    private final RandomSupport randomSupport;
    private final GenerationalFloatFactory maximumRateFloatFactory;

    public GenerationalIsLessThanRandomGate(final RandomSupport randomSupport, final FloatFactory maximumRateFloatFactory) {
        this(randomSupport, new GenerationalFloatFactory(maximumRateFloatFactory));
    }

    @Override
    public void reinitialize() {
        maximumRateFloatFactory.reinitialize();
    }

    @Override
    public boolean isOn() {
        float maximumRate = maximumRateFloatFactory.getValue();

        return Float.compare(maximumRate, 0f) > 0 && randomSupport.isLessThan(maximumRate);
    }
}
