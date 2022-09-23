package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.fitness.FitnessControllerFactory;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.data.structure.collection.IterableArray;
import lombok.Getter;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

final class InitializationContext {
    private static final RandomSupportFactory RANDOM_SUPPORT_FACTORY = RandomSupportFactory.getInstance();
    private final GeneralSettings general;
    @Getter
    private final NeatEnvironmentType environmentType;
    @Getter
    private final Set<Long> threadIds;
    private final RandomnessSettings randomness;
    @Getter
    private final HistoricalMarkings historicalMarkings;
    private final Map<Object, Object> singletonContainers;

    InitializationContext(final GeneralSettings general, final ParallelismSettings parallelism, final RandomnessSettings randomness) {
        this.general = general;
        this.environmentType = NeatEnvironmentType.from(general.getFitnessFunction());
        this.threadIds = parallelism.getThreadIds();
        this.randomness = randomness;
        this.historicalMarkings = new HistoricalMarkings();
        this.singletonContainers = new IdentityHashMap<>();
    }

    public NeatEnvironment getFitnessFunction() {
        return general.getFitnessFunction();
    }

    public <T> IterableArray<T> createPopulationArray() {
        return new IterableArray<>(general.getPopulationSize());
    }

    public FitnessControllerFactory getFitnessControllerFactory() {
        return general.getFitnessControllerFactory();
    }

    public RandomSupport createRandomSupport(final RandomType randomType) {
        return RANDOM_SUPPORT_FACTORY.create(randomType);
    }

    public RandomSupport createDefaultRandomSupport() {
        return createRandomSupport(randomness.getType());
    }

    private <TValueProvider, TValue> TValue provideSingleton(final TValueProvider valueProvider, final ValueFactory<TValue> valueFactory) {
        if (!singletonContainers.containsKey(valueProvider)) {
            TValue value = valueFactory.create();

            singletonContainers.put(valueProvider, value);

            return value;
        }

        return (TValue) singletonContainers.get(valueProvider);
    }

    public int provideSingleton(final IntegerNumber integerNumber) {
        return provideSingleton(integerNumber, () -> integerNumber.createFactory(this).create());
    }

    public float provideSingleton(final FloatNumber floatNumber) {
        return provideSingleton(floatNumber, () -> floatNumber.createFactory(this).create());
    }

    public <T extends Enum<T>> T provideSingleton(final EnumValue<T> enumValue) {
        return provideSingleton(enumValue, () -> enumValue.createFactory(this).create());
    }

    @FunctionalInterface
    private interface ValueFactory<T> {
        T create();
    }
}
