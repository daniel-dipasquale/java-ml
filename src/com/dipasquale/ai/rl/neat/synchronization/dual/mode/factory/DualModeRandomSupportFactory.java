package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.random.float1.DefaultRandomSupport;
import com.dipasquale.common.random.float1.MeanDistributedRandomSupport;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.ThreadLocalRandomSupport;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeRandomSupportFactory implements Serializable {
    @Serial
    private static final long serialVersionUID = 4271619355783535783L;
    private static final RandomSupport RANDOM_SUPPORT_UNIFORM_CONCURRENT = new ThreadLocalRandomSupport();
    private static final RandomSupport RANDOM_SUPPORT_UNIFORM = new DefaultRandomSupport();
    private static final RandomSupport RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = new MeanDistributedRandomSupport(RANDOM_SUPPORT_UNIFORM_CONCURRENT, 5);
    private static final RandomSupport RANDOM_SUPPORT_MEAN_DISTRIBUTED = new MeanDistributedRandomSupport(RANDOM_SUPPORT_UNIFORM, 5);
    private static final DualModeRandomSupportFactory INSTANCE = new DualModeRandomSupportFactory();

    public DualModeRandomSupport create(final int concurrencyLevel, final RandomType type) {
        RandomSupport concurrentRandomSupport = new ProxyRandomSupport(type, true);
        RandomSupport defaultRandomSupport = new ProxyRandomSupport(type, false);

        return new DualModeRandomSupport(concurrencyLevel, concurrentRandomSupport, defaultRandomSupport);
    }

    public static DualModeRandomSupportFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    private static RandomSupport getRandomSupport(final RandomType type, final boolean concurrent) {
        if (!concurrent) {
            return switch (type) {
                case UNIFORM -> RANDOM_SUPPORT_UNIFORM;

                case MEAN_DISTRIBUTED -> RANDOM_SUPPORT_MEAN_DISTRIBUTED;
            };
        }

        return switch (type) {
            case UNIFORM -> RANDOM_SUPPORT_UNIFORM_CONCURRENT;

            case MEAN_DISTRIBUTED -> RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT;
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ProxyRandomSupport implements RandomSupport, Serializable {
        @Serial
        private static final long serialVersionUID = -7686659895045446750L;
        private final RandomType type;
        private final boolean concurrent;

        @Override
        public float next() {
            return getRandomSupport(type, concurrent).next();
        }
    }
}
