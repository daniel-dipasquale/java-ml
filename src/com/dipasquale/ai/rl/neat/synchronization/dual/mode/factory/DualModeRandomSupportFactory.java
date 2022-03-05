package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.rl.neat.RandomType;
import com.dipasquale.common.random.float1.BellCurveRandomSupport;
import com.dipasquale.common.random.float1.QuadrupleSigmoidRandomSupport;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.common.random.float1.concurrent.ThreadLocalUniformRandomSupport;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeRandomSupportFactory implements Serializable {
    @Serial
    private static final long serialVersionUID = 4271619355783535783L;
    private static final DualModeRandomSupportFactory INSTANCE = new DualModeRandomSupportFactory();

    private static RandomSupport createRandomSupport(final RandomType type, final RandomSupport randomSupport) {
        return switch (type) {
            case UNIFORM -> randomSupport;

            case BELL_CURVE -> new BellCurveRandomSupport(randomSupport, 5);

            case QUADRUPLE_SIGMOID -> new QuadrupleSigmoidRandomSupport(randomSupport, 0.91f, 0.1f, (float) Math.pow(10D, 1.75D));

            case QUADRUPLE_STEEPENED_SIGMOID -> new QuadrupleSigmoidRandomSupport(randomSupport, 0.99f, 0.05f, (float) Math.pow(10D, 3D));
        };
    }

    private static RandomSupport createRandomSupport(final boolean concurrent, final RandomType type) {
        if (!concurrent) {
            return createRandomSupport(type, new UniformRandomSupport());
        }

        return createRandomSupport(type, ThreadLocalUniformRandomSupport.getInstance());
    }

    public DualModeRandomSupport create(final int concurrencyLevel, final RandomType type) {
        RandomSupport concurrentRandomSupport = createRandomSupport(true, type);
        RandomSupport defaultRandomSupport = createRandomSupport(false, type);

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
