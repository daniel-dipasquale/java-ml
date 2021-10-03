package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeBoundedRandomIntegerFactory;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.factory.LiteralIntegerFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeIntegerFactory;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegerNumber {
    private final DualModeFactoryCreator factoryCreator;
    private Integer singletonValue = null;

    public static IntegerNumber literal(final int value) {
        DualModeFactoryCreator factoryCreator = (ps, rs) -> {
            DualModeIntegerFactory integerFactory = new DualModeIntegerFactory(ps.getConcurrencyLevel(), new LiteralIntegerFactory(value));

            return new DefaultDualModeFactory<>(integerFactory);
        };

        return new IntegerNumber(factoryCreator);
    }

    public static IntegerNumber random(final RandomType type, final int min, final int max) {
        DualModeFactoryCreator factoryCreator = (ps, rs) -> {
            DualModeBoundedRandomIntegerFactory integerFactory = new DualModeBoundedRandomIntegerFactory(rs.get(type), min, max);

            return new DefaultDualModeFactory<>(integerFactory);
        };

        return new IntegerNumber(factoryCreator);
    }

    public DualModeFactory createFactory(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        return factoryCreator.create(parallelismSupport, randomSupports);
    }

    public int getSingletonValue(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        if (singletonValue == null) {
            singletonValue = factoryCreator.create(parallelismSupport, randomSupports).create();
        }

        return singletonValue;
    }

    public interface DualModeFactory extends IntegerFactory, DualModeObject {
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultDualModeFactory<T extends IntegerFactory & DualModeObject> implements DualModeFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 3324024183810540982L;
        private final T integerFactory;

        @Override
        public int create() {
            return integerFactory.create();
        }

        @Override
        public int concurrencyLevel() {
            return integerFactory.concurrencyLevel();
        }

        @Override
        public void activateMode(final int concurrencyLevel) {
            integerFactory.activateMode(concurrencyLevel);
        }
    }

    @FunctionalInterface
    private interface DualModeFactoryCreator {
        DualModeFactory create(ParallelismSupport parallelismSupport, Map<RandomType, DualModeRandomSupport> randomSupports);
    }
}
