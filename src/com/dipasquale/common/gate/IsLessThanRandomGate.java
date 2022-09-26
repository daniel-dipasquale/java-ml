package com.dipasquale.common.gate;

import com.dipasquale.common.factory.ConstantFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class IsLessThanRandomGate implements Gate, Serializable {
    @Serial
    private static final long serialVersionUID = -3883271729484274647L;
    private final RandomSupport randomSupport;
    private final FloatFactory maximumRateFloatFactory;

    public IsLessThanRandomGate(final RandomSupport randomSupport, final float maximumRate) {
        this(randomSupport, new ConstantFloatFactory(maximumRate));
    }

    @Override
    public boolean isOn() {
        float maximumRate = maximumRateFloatFactory.create();

        return Float.compare(maximumRate, 0f) > 0 && randomSupport.isLessThan(maximumRate);
    }
}
