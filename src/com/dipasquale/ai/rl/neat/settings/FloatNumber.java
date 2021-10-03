package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeBoundedRandomFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.LiteralFloatFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeFloatFactory;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FloatNumber {
    private final DualModeFactoryCreator factoryCreator;
    private Float singletonValue = null;

    public static <T extends FloatFactory & DualModeObject> DualModeFactory createFactory(final T floatFactory) {
        return new DefaultDualModeFactory<>(floatFactory);
    }

    public static FloatNumber literal(final float value) {
        DualModeFactoryCreator factoryCreator = (ps, rs) -> createFactory(new DualModeFloatFactory(ps.getConcurrencyLevel(), new LiteralFloatFactory(value)));

        return new FloatNumber(factoryCreator);
    }

    public static FloatNumber random(final RandomType type, final float min, final float max) {
        DualModeFactoryCreator factoryCreator = (ps, rs) -> createFactory(new DualModeBoundedRandomFloatFactory(rs.get(type), min, max));

        return new FloatNumber(factoryCreator);
    }

    public DualModeFactory createFactory(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        return factoryCreator.create(parallelismSupport, randomSupports);
    }

    public float getSingletonValue(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        if (singletonValue == null) {
            singletonValue = factoryCreator.create(parallelismSupport, randomSupports).create();
        }

        return singletonValue;
    }

    public interface DualModeFactory extends FloatFactory, DualModeObject {
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultDualModeFactory<T extends FloatFactory & DualModeObject> implements DualModeFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 3324024183810540982L;
        private final T floatFactory;

        @Override
        public float create() {
            return floatFactory.create();
        }

        @Override
        public int concurrencyLevel() {
            return floatFactory.concurrencyLevel();
        }

        @Override
        public void activateMode(final int concurrencyLevel) {
            floatFactory.activateMode(concurrencyLevel);
        }
    }

    @FunctionalInterface
    private interface DualModeFactoryCreator {
        DualModeFactory create(ParallelismSupport parallelismSupport, Map<RandomType, DualModeRandomSupport> randomSupports);
    }
}
