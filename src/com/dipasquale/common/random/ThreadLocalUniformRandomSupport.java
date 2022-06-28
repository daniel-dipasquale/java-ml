package com.dipasquale.common.random;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadLocalUniformRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 6937713928633169577L;
    private static final ThreadLocalUniformRandomSupport INSTANCE = new ThreadLocalUniformRandomSupport();

    public static ThreadLocalUniformRandomSupport getInstance() {
        return INSTANCE;
    }

    @Override
    public float nextFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    @Override
    public double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
