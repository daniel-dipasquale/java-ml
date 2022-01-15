package com.dipasquale.common.random.float1;

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

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public float next() {
        return ThreadLocalRandom.current().nextFloat();
    }
}
