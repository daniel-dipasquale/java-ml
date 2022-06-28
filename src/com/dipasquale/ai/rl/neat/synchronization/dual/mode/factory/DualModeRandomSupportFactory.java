package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.rl.neat.RandomType;
import com.dipasquale.common.random.BellCurveRandomSupport;
import com.dipasquale.common.random.QuadrupleSigmoidRandomSupport;
import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.common.random.UniformRandomSupport;
import com.dipasquale.synchronization.dual.mode.random.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeRandomSupportFactory implements Serializable {
    @Serial
    private static final long serialVersionUID = 4271619355783535783L;
    private static final int BELL_CURVE_ITERATIONS = 5;
    private static final double QUADRUPLE_SIGMOID_PLATEAU_AREA_RATE = 0.5D;
    private static final double QUADRUPLE_SIGMOID_PLATEAU_RANGE_RATE = 0.5D;
    private static final double QUADRUPLE_SIGMOID_STEEPENED_RATE = Math.pow(10D, 1.5D);
    private static final double QUADRUPLE_STEEPENED_SIGMOID_PLATEAU_AREA_RATE = 0.91D;
    private static final double QUADRUPLE_STEEPENED_SIGMOID_PLATEAU_RANGE_RATE = 0.1D;
    private static final double QUADRUPLE_STEEPENED_SIGMOID_STEEPENED_RATE = Math.pow(10D, 1.75D);
    private static final DualModeRandomSupportFactory INSTANCE = new DualModeRandomSupportFactory();

    private static RandomSupport createRandomSupport(final RandomType type) {
        RandomSupport randomSupport = new UniformRandomSupport();

        return switch (type) {
            case UNIFORM -> randomSupport;

            case BELL_CURVE -> new BellCurveRandomSupport(randomSupport, BELL_CURVE_ITERATIONS);

            case QUADRUPLE_SIGMOID -> new QuadrupleSigmoidRandomSupport(randomSupport, QUADRUPLE_SIGMOID_PLATEAU_AREA_RATE, QUADRUPLE_SIGMOID_PLATEAU_RANGE_RATE, QUADRUPLE_SIGMOID_STEEPENED_RATE);

            case QUADRUPLE_STEEPENED_SIGMOID -> new QuadrupleSigmoidRandomSupport(randomSupport, QUADRUPLE_STEEPENED_SIGMOID_PLATEAU_AREA_RATE, QUADRUPLE_STEEPENED_SIGMOID_PLATEAU_RANGE_RATE, QUADRUPLE_STEEPENED_SIGMOID_STEEPENED_RATE);
        };
    }

    public DualModeRandomSupport create(final int concurrencyLevel, final RandomType type) {
        RandomSupport concurrentRandomSupport = createRandomSupport(type);
        RandomSupport defaultRandomSupport = createRandomSupport(type);

        return new DualModeRandomSupport(concurrencyLevel, concurrentRandomSupport, defaultRandomSupport);
    }

    public static DualModeRandomSupportFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
