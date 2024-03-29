package com.dipasquale.common.random;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

public final class UniformRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 1127495316340478517L;
    private final Random random;

    public UniformRandomSupport() {
        this.random = new Random();
    }

    public UniformRandomSupport(final long seed) {
        this.random = new Random(seed);
    }

    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }
}
