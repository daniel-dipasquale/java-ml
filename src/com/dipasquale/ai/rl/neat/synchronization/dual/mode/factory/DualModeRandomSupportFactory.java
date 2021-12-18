package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.random.float1.BellCurveRandomSupport;
import com.dipasquale.common.random.float1.QuadrupleSigmoidRandomSupport;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.ThreadLocalUniformRandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
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
    private static final RandomSupport RANDOM_SUPPORT_UNIFORM_CONCURRENT = new ThreadLocalUniformRandomSupport();
    private static final RandomSupport RANDOM_SUPPORT_UNIFORM = new UniformRandomSupport();
    private static final RandomSupport RANDOM_SUPPORT_BELL_CURVE_CONCURRENT = new BellCurveRandomSupport(new ThreadLocalUniformRandomSupport(), 5);
    private static final RandomSupport RANDOM_SUPPORT_BELL_CURVE = new BellCurveRandomSupport(new UniformRandomSupport(), 5);
    private static final RandomSupport RANDOM_SUPPORT_QUADRUPLE_SIGMOID_CONCURRENT = new QuadrupleSigmoidRandomSupport(new ThreadLocalUniformRandomSupport(), 0.91f, 0.1f, (float) Math.pow(10D, 1.75D));
    private static final RandomSupport RANDOM_SUPPORT_QUADRUPLE_SIGMOID = new QuadrupleSigmoidRandomSupport(new UniformRandomSupport(), 0.91f, 0.1f, (float) Math.pow(10D, 1.75D));
    private static final RandomSupport RANDOM_SUPPORT_QUADRUPLE_STEEPENED_SIGMOID_CONCURRENT = new QuadrupleSigmoidRandomSupport(new ThreadLocalUniformRandomSupport(), 0.99f, 0.05f, (float) Math.pow(10D, 3D));
    private static final RandomSupport RANDOM_SUPPORT_QUADRUPLE_STEEPENED_SIGMOID = new QuadrupleSigmoidRandomSupport(new UniformRandomSupport(), 0.99f, 0.05f, (float) Math.pow(10D, 3D));
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

                case BELL_CURVE -> RANDOM_SUPPORT_BELL_CURVE;

                case QUADRUPLE_SIGMOID -> RANDOM_SUPPORT_QUADRUPLE_SIGMOID;

                case QUADRUPLE_STEEPENED_SIGMOID -> RANDOM_SUPPORT_QUADRUPLE_STEEPENED_SIGMOID;
            };
        }

        return switch (type) {
            case UNIFORM -> RANDOM_SUPPORT_UNIFORM_CONCURRENT;

            case BELL_CURVE -> RANDOM_SUPPORT_BELL_CURVE_CONCURRENT;

            case QUADRUPLE_SIGMOID -> RANDOM_SUPPORT_QUADRUPLE_SIGMOID_CONCURRENT;

            case QUADRUPLE_STEEPENED_SIGMOID -> RANDOM_SUPPORT_QUADRUPLE_STEEPENED_SIGMOID_CONCURRENT;
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
