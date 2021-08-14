package com.dipasquale.common.random.float2;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

public final class DefaultRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 1127495316340478517L;
    private final Random random;

    public DefaultRandomSupport() {
        this.random = new Random();
    }

    public DefaultRandomSupport(final long seed) {
        this.random = new Random(seed);
    }

    @Override
    public double next() {
        return random.nextDouble();
    }
}
