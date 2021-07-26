package com.dipasquale.common.random.float1;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public final class ThreadLocalRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 6937713928633169577L;

    @Override
    public float next() {
        return ThreadLocalRandom.current().nextFloat();
    }
}
