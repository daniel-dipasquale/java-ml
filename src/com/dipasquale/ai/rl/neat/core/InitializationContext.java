package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomSupportFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDeque;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDequeFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeMapToSetFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeSet;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.Getter;

import java.util.IdentityHashMap;
import java.util.Map;

public final class InitializationContext {
    private static final DualModeRandomSupportFactory RANDOM_SUPPORT_FACTORY = DualModeRandomSupportFactory.getInstance();
    @Getter
    private final NeatEnvironmentType environmentType;
    @Getter
    private final int concurrencyLevel;
    private final int maximumConcurrencyLevel;
    private final DualModeMapFactory mapFactory;
    private final DualModeMapToSetFactory setFactory;
    private final DualModeDequeFactory dequeFactory;
    private final RandomSupport random;
    private final Map<Object, Object> singletons;

    InitializationContext(final NeatEnvironmentType environmentType, final ParallelismSupport parallelism, final RandomSupport random) {
        int concurrencyLevel = parallelism.getConcurrencyLevel();
        int maximumConcurrencyLevel = getMaximumConcurrencyLevel(concurrencyLevel);
        DualModeMapFactory mapFactory = new DualModeMapFactory(concurrencyLevel, maximumConcurrencyLevel);

        this.environmentType = environmentType;
        this.concurrencyLevel = concurrencyLevel;
        this.maximumConcurrencyLevel = maximumConcurrencyLevel;
        this.mapFactory = mapFactory;
        this.setFactory = new DualModeMapToSetFactory(mapFactory);
        this.dequeFactory = new DualModeDequeFactory(concurrencyLevel, maximumConcurrencyLevel);
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
        return new DualModeMap<>(mapFactory);
    }

    public DualModeMapToSetFactory createSetFactory() {
        return new DualModeMapToSetFactory(createMapFactory());
    }

    public <T> DualModeSet<T, DualModeMapToSetFactory> createSet() {
        return new DualModeSet<>(setFactory);
    }

    public <T> DualModeDeque<T, DualModeDequeFactory> createDeque() {
        return new DualModeDeque<>(dequeFactory);
    }

    public DualModeRandomSupport createRandomSupport(final RandomType randomType) {
        return RANDOM_SUPPORT_FACTORY.create(concurrencyLevel, randomType);
    }

    public DualModeRandomSupport createDefaultRandomSupport() {
        return createRandomSupport(random.getType());
    }

    public int getIntegerSingleton(final IntegerNumber integerNumber) {
        if (!singletons.containsKey(integerNumber)) {
            int value = integerNumber.createFactory(this).create();

            singletons.put(integerNumber, value);

            return value;
        }

        return (int) singletons.get(integerNumber);
    }

    public float getFloatSingleton(final FloatNumber floatNumber) {
        if (!singletons.containsKey(floatNumber)) {
            float value = floatNumber.createFactory(this).create();

            singletons.put(floatNumber, value);

            return value;
        }

        return (float) singletons.get(floatNumber);
    }

    public <T extends Enum<T>> T getEnumSingleton(final EnumValue<T> enumValue) {
        if (!singletons.containsKey(enumValue)) {
            T value = enumValue.createFactory(this).create();

            singletons.put(enumValue, value);

            return value;
        }

        return (T) singletons.get(enumValue);
    }
}
