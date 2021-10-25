package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.common.WeightMutationType;
import com.dipasquale.ai.rl.neat.core.FloatNumber;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.MutationSupport;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeOutputClassifierFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomFloatFactory;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.provider.DualModeIsLessThanRandomGateProvider;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextMutationSupport implements Context.MutationSupport {
    private DualModeIsLessThanRandomGateProvider shouldAddNodeGateProvider;
    private DualModeIsLessThanRandomGateProvider shouldAddConnectionGateProvider;
    private DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> weightMutationTypeFactory;
    private DualModeIsLessThanRandomGateProvider shouldDisableExpressedConnectionGateProvider;

    private static DualModeIsLessThanRandomGateProvider createIsLessThanGateProvider(final InitializationContext initializationContext, final FloatNumber max) {
        return new DualModeIsLessThanRandomGateProvider(initializationContext.getRandomSupport(), max.getSingletonValue(initializationContext));
    }

    private static DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> createWeightMutationTypeFactory(final InitializationContext initializationContext, final FloatNumber perturbWeightRate, final FloatNumber replaceWeightRate) {
        float perturbRate = perturbWeightRate.getSingletonValue(initializationContext);
        float replaceRate = replaceWeightRate.getSingletonValue(initializationContext);
        float totalRate = (float) Math.ceil(perturbRate + replaceRate);
        OutputClassifier<WeightMutationType> weightMutationTypeClassifier = new OutputClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            weightMutationTypeClassifier.addRangeFor(perturbRate / totalRate, WeightMutationType.PERTURB);
            weightMutationTypeClassifier.addRangeFor(replaceRate / totalRate, WeightMutationType.REPLACE);
        }

        weightMutationTypeClassifier.addRemainingRangeFor(WeightMutationType.NONE);

        return new DualModeOutputClassifierFactory<>(new DualModeRandomFloatFactory(initializationContext.getRandomSupport()), weightMutationTypeClassifier);
    }

    public static DefaultContextMutationSupport create(final InitializationContext initializationContext, final MutationSupport mutationSupport) {
        DualModeIsLessThanRandomGateProvider shouldAddNodeGateProvider = createIsLessThanGateProvider(initializationContext, mutationSupport.getAddNodeRate());
        DualModeIsLessThanRandomGateProvider shouldAddConnectionGateProvider = createIsLessThanGateProvider(initializationContext, mutationSupport.getAddConnectionRate());
        DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> weightMutationTypeFactory = createWeightMutationTypeFactory(initializationContext, mutationSupport.getPerturbWeightRate(), mutationSupport.getReplaceWeightRate());
        DualModeIsLessThanRandomGateProvider shouldDisableExpressedConnectionGateProvider = createIsLessThanGateProvider(initializationContext, mutationSupport.getDisableExpressedConnectionRate());

        return new DefaultContextMutationSupport(shouldAddNodeGateProvider, shouldAddConnectionGateProvider, weightMutationTypeFactory, shouldDisableExpressedConnectionGateProvider);
    }

    @Override
    public boolean shouldAddNode() {
        return shouldAddNodeGateProvider.isOn();
    }

    @Override
    public boolean shouldAddConnection() {
        return shouldAddConnectionGateProvider.isOn();
    }

    @Override
    public WeightMutationType generateWeightMutationType() {
        return weightMutationTypeFactory.create();
    }

    @Override
    public boolean shouldDisableExpressedConnection() {
        return shouldDisableExpressedConnectionGateProvider.isOn();
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("mutation.shouldAddNodeGateProvider", shouldAddNodeGateProvider);
        stateGroup.put("mutation.shouldAddConnectionGateProvider", shouldAddConnectionGateProvider);
        stateGroup.put("mutation.weightMutationTypeFactory", weightMutationTypeFactory);
        stateGroup.put("mutation.shouldDisableExpressedConnectionGateProvider", shouldDisableExpressedConnectionGateProvider);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        shouldAddNodeGateProvider = DualModeObject.activateMode(stateGroup.get("mutation.shouldAddNodeGateProvider"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        shouldAddConnectionGateProvider = DualModeObject.activateMode(stateGroup.get("mutation.shouldAddConnectionGateProvider"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        weightMutationTypeFactory = DualModeObject.activateMode(stateGroup.get("mutation.weightMutationTypeFactory"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        shouldDisableExpressedConnectionGateProvider = DualModeObject.activateMode(stateGroup.get("mutation.shouldDisableExpressedConnectionGateProvider"), ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
