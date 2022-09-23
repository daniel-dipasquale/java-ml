package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class BoundedRandomFloatFactory implements FloatFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -791004180256061334L;
    private final RandomSupport randomSupport;
    private final float minimum;
    private final float maximum;

    @Override
    public float create() {
        return randomSupport.nextFloat(minimum, maximum);
    }
}
