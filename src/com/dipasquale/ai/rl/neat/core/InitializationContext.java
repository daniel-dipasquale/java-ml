package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomSupportFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDeque;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDequeFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeMapToSetFactory;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.Getter;

import java.util.IdentityHashMap;
import java.util.Map;

final class InitializationContext {
    private static final DualModeRandomSupportFactory RANDOM_SUPPORT_FACTORY = DualModeRandomSupportFactory.getInstance();
    @Getter
    private final NeatEnvironmentType environmentType;
    @Getter
    private final int concurrencyLevel;
    private final int maximumConcurrencyLevel;
    private final RandomSupport random;
    private final Map<Object, Object> singletons;

    InitializationContext(final NeatEnvironmentType environmentType, final ParallelismSupport parallelism, final RandomSupport random) {
        int concurrencyLevel = parallelism.getConcurrencyLevel();

        this.environmentType = environmentType;
        this.concurrencyLevel = concurrencyLevel;
        this.maximumConcurrencyLevel = getMaximumConcurrencyLevel(concurrencyLevel);
        this.random = random;
        this.singletons = new IdentityHashMap<>();
    }

    private static int getMaximumConcurrencyLevel(final int concurrencyLevel) {
        int availableProcessors = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

        return Math.max(concurrencyLevel, availableProcessors);
    }

    public DualModeMapFactory createMapFactory() {
        return new DualModeMapFactory(concurrencyLevel, maximumConcurrencyLevel);
    }

    public <TKey, TValue> DualModeMap<TKey, TValue, DualModeMapFactory> createMap() {
        return new DualModeMap<>(createMapFactory());
    }

    public DualModeMapToSetFactory createSetFactory() {
        return new DualModeMapToSetFactory(createMapFactory());
    }

    public DualModeDequeFactory createDequeFactory() {
        return new DualModeDequeFactory(concurrencyLevel, maximumConcurrencyLevel);
    }

    public <T> DualModeDeque<T, DualModeDequeFactory> createDeque() {
        return new DualModeDeque<>(createDequeFactory());
    }

    public DualModeRandomSupport createRandomSupport(final RandomType randomType) {
        return RANDOM_SUPPORT_FACTORY.create(concurrencyLevel, randomType);
    }

    public DualModeRandomSupport createDefaultRandomSupport() {
        return createRandomSupport(random.getType());
    }

    private <TValueProvider, TValue> TValue getSingletonValue(final TValueProvider valueProvider, final ValueFactory<TValue> valueFactory) {
        if (!singletons.containsKey(valueProvider)) {
            TValue value = valueFactory.create();

            singletons.put(valueProvider, value);

            return value;
        }

        return (TValue) singletons.get(valueProvider);
    }

    public int getIntegerSingleton(final IntegerNumber integerNumber) {
        return getSingletonValue(integerNumber, () -> integerNumber.createFactory(this).create());
    }

    public float getFloatSingleton(final FloatNumber floatNumber) {
        return getSingletonValue(floatNumber, () -> floatNumber.createFactory(this).create());
    }

    public <T extends Enum<T>> T getEnumSingleton(final EnumValue<T> enumValue) {
        return getSingletonValue(enumValue, () -> enumValue.createFactory(this).create());
    }

    @FunctionalInterface
    private interface ValueFactory<T> {
        T create();
    }
}
