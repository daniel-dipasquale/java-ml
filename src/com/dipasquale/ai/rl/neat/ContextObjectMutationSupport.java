package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeOutputClassifierFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomFloatFactory;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.gate.DualModeIsLessThanRandomGate;
import com.dipasquale.synchronization.event.loop.BatchingEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectMutationSupport implements Context.MutationSupport {
    private DualModeIsLessThanRandomGate shouldAddNodeGate;
    private DualModeIsLessThanRandomGate shouldAddConnectionGate;
    private DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> weightMutationTypeFactory;
    private DualModeIsLessThanRandomGate shouldDisableExpressedConnectionGate;

    private static DualModeIsLessThanRandomGate createIsLessThanGate(final InitializationContext initializationContext, final FloatNumber maximum) {
        return new DualModeIsLessThanRandomGate(initializationContext.createDefaultRandomSupport(), initializationContext.getFloatSingleton(maximum));
    }

    private static DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> createWeightMutationTypeFactory(final InitializationContext initializationContext, final FloatNumber perturbWeightRate, final FloatNumber replaceWeightRate) {
        float perturbRate = initializationContext.getFloatSingleton(perturbWeightRate);
        float replaceRate = initializationContext.getFloatSingleton(replaceWeightRate);
        float totalRate = (float) Math.ceil(perturbRate + replaceRate);
        ProbabilityClassifier<WeightMutationType> weightMutationTypeClassifier = new ProbabilityClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            weightMutationTypeClassifier.add(perturbRate / totalRate, WeightMutationType.PERTURB);
            weightMutationTypeClassifier.add(replaceRate / totalRate, WeightMutationType.REPLACE);
            weightMutationTypeClassifier.add(1f - (perturbRate + replaceRate) / totalRate, WeightMutationType.NONE);
        } else {
            weightMutationTypeClassifier.add(1f, WeightMutationType.NONE);
        }

        return new DualModeOutputClassifierFactory<>(new DualModeRandomFloatFactory(initializationContext.createDefaultRandomSupport()), weightMutationTypeClassifier);
    }

    static ContextObjectMutationSupport create(final InitializationContext initializationContext, final MutationSupport mutationSupport) {
        DualModeIsLessThanRandomGate shouldAddNodeGate = createIsLessThanGate(initializationContext, mutationSupport.getAddNodeRate());
        DualModeIsLessThanRandomGate shouldAddConnectionGate = createIsLessThanGate(initializationContext, mutationSupport.getAddConnectionRate());
        DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> weightMutationTypeFactory = createWeightMutationTypeFactory(initializationContext, mutationSupport.getPerturbWeightRate(), mutationSupport.getReplaceWeightRate());
        DualModeIsLessThanRandomGate shouldDisableExpressedConnectionGate = createIsLessThanGate(initializationContext, mutationSupport.getDisableExpressedConnectionRate());

        return new ContextObjectMutationSupport(shouldAddNodeGate, shouldAddConnectionGate, weightMutationTypeFactory, shouldDisableExpressedConnectionGate);
    }

    @Override
    public boolean shouldAddNode() {
        return shouldAddNodeGate.isOn();
    }

    @Override
    public boolean shouldAddConnection() {
        return shouldAddConnectionGate.isOn();
    }

    @Override
    public WeightMutationType generateWeightMutationType() {
        return weightMutationTypeFactory.create();
    }

    @Override
    public boolean shouldDisableExpressedConnection() {
        return shouldDisableExpressedConnectionGate.isOn();
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("mutation.shouldAddNodeGate", shouldAddNodeGate);
        stateGroup.put("mutation.shouldAddConnectionGate", shouldAddConnectionGate);
        stateGroup.put("mutation.weightMutationTypeFactory", weightMutationTypeFactory);
        stateGroup.put("mutation.shouldDisableExpressedConnectionGate", shouldDisableExpressedConnectionGate);
    }

    private void load(final SerializableStateGroup stateGroup, final int concurrencyLevel) {
        shouldAddNodeGate = DualModeObject.activateMode(stateGroup.get("mutation.shouldAddNodeGate"), concurrencyLevel);
        shouldAddConnectionGate = DualModeObject.activateMode(stateGroup.get("mutation.shouldAddConnectionGate"), concurrencyLevel);
        weightMutationTypeFactory = DualModeObject.activateMode(stateGroup.get("mutation.weightMutationTypeFactory"), concurrencyLevel);
        shouldDisableExpressedConnectionGate = DualModeObject.activateMode(stateGroup.get("mutation.shouldDisableExpressedConnectionGate"), concurrencyLevel);
    }

    public void load(final SerializableStateGroup stateGroup, final BatchingEventLoop eventLoop) {
        load(stateGroup, ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
