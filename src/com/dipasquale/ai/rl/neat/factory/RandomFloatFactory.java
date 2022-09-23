package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RandomFloatFactory implements FloatFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 796774320554634960L;
    private final RandomSupport randomSupport;

    @Override
    public float create() {
        return randomSupport.nextFloat();
    }
}
