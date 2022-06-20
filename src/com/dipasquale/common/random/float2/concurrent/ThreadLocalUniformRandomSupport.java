package com.dipasquale.common.random.float2.concurrent;

import com.dipasquale.common.random.float2.RandomSupport;
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
    public double next() {
        return ThreadLocalRandom.current().nextDouble();
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
