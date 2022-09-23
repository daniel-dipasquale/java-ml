package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class BoundedRandomIntegerFactory implements IntegerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 3322232093472816035L;
    private final RandomSupport randomSupport;
    private final int minimum;
    private final int maximum;

    @Override
    public int create() {
        return randomSupport.nextInteger(minimum, maximum);
    }
}
