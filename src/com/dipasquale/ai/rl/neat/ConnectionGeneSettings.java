package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.NoopRecurrentWeightFactory;
import com.dipasquale.ai.rl.neat.factory.RecurrentWeightFactory;
import com.dipasquale.ai.rl.neat.factory.StrategyRecurrentWeightFactory;
import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.generational.gate.GenerationalIsLessThanRandomGate;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ConstantFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ConnectionGeneSettings {
    @Builder.Default
    private final FloatNumber weightFactory = FloatNumber.random(RandomType.BELL_CURVE, 2f);
    @Builder.Default
    private final FloatNumber weightPerturber = FloatNumber.constant(2.5f);
    @Getter
    @Builder.Default
    private final RecurrentStateType recurrentStateType = RecurrentStateType.DEFAULT;
    @Getter
    @Builder.Default
    private final float recurrentAllowanceRate = 0.2f;
    @Builder.Default
    private final float unrestrictedDirectionAllowanceRate = 0.5f;
    @Builder.Default
    private final float multiCycleAllowanceRate = 0f;

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "FloatFactoryBuilder", builderMethodName = "floatFactoryBuilder")
    private static FloatFactory createFloatFactory(final NeatInitializationContext initializationContext, final FloatNumber floatNumber, final String name) {
        return floatNumber.createFactory(initializationContext, -Float.MAX_VALUE, Float.MAX_VALUE, name);
    }

    private static RecurrentWeightFactory createRecurrentWeightFactory(final ConnectionGeneSettings connectionGeneSettings, final NeatInitializationContext initializationContext) {
        float recurrentAllowanceRate = connectionGeneSettings.recurrentAllowanceRate;

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return NoopRecurrentWeightFactory.getInstance();
        }

        FloatFactory weightFloatFactory = floatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(connectionGeneSettings.weightFactory)
                .name("connections.weightFactory")
                .build();

        return new StrategyRecurrentWeightFactory(weightFloatFactory, connectionGeneSettings.recurrentStateType);
    }

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "WeightPerturberBuilder", builderMethodName = "weightPerturberBuilder")
    private static WeightPerturber createWeightPerturber(final NeatInitializationContext initializationContext, final FloatNumber weightPerturberFloatNumber) {
        FloatFactory floatFactory = weightPerturberFloatNumber.createFactory(initializationContext, -Float.MAX_VALUE, Float.MAX_VALUE, "connections.weightPerturber");

        return new WeightPerturber(floatFactory);
    }

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "IsLessThanGateBuilder", builderMethodName = "isLessThanGateBuilder")
    private static GenerationalIsLessThanRandomGate createIsLessThanGate(final RandomSupport randomSupport, final float maximumRate, final String name) {
        FloatFactory maximumRateFloatFactory = new ConstantFloatFactory(maximumRate);

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(maximumRate, 0f, name);
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(maximumRate, 1f, name);

        return new GenerationalIsLessThanRandomGate(randomSupport, maximumRateFloatFactory);
    }

    DefaultNeatContextConnectionGeneSupport create(final NeatInitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate) {
        FloatFactory fixedWeightFactory = floatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(weightFactory)
                .name("connections.weightFactory")
                .build();

        RecurrentWeightFactory recurrentWeightFactory = createRecurrentWeightFactory(this, initializationContext);

        WeightPerturber fixedWeightPerturber = weightPerturberBuilder()
                .initializationContext(initializationContext)
                .weightPerturberFloatNumber(weightPerturber)
                .build();

        GenerationalIsLessThanRandomGate shouldAllowRecurrentGate = isLessThanGateBuilder()
                .randomSupport(initializationContext.createDefaultRandomSupport())
                .maximumRate(recurrentAllowanceRate)
                .name("connections.recurrentAllowanceRate")
                .build();

        GenerationalIsLessThanRandomGate shouldAllowUnrestrictedDirectionGate = isLessThanGateBuilder()
                .randomSupport(initializationContext.createDefaultRandomSupport())
                .maximumRate(unrestrictedDirectionAllowanceRate)
                .name("connections.unrestrictedDirectionAllowanceRate")
                .build();

        GenerationalIsLessThanRandomGate shouldAllowMultiCycleGate = isLessThanGateBuilder()
                .randomSupport(initializationContext.createDefaultRandomSupport())
                .maximumRate(multiCycleAllowanceRate)
                .name("connections.multiCycleAllowanceRate")
                .build();

        GenesisGenomeConnector genesisGenomeConnector = genesisGenomeTemplate.createConnector(fixedWeightFactory);
        HistoricalMarkings historicalMarkings = initializationContext.getHistoricalMarkings();

        return new DefaultNeatContextConnectionGeneSupport(fixedWeightFactory, recurrentWeightFactory, fixedWeightPerturber, shouldAllowRecurrentGate, shouldAllowUnrestrictedDirectionGate, shouldAllowMultiCycleGate, genesisGenomeConnector, historicalMarkings);
    }
}
