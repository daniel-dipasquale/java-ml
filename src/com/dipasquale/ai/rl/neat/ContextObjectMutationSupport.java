package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.OutputClassifierFactory;
import com.dipasquale.ai.rl.neat.factory.RandomFloatFactory;
import com.dipasquale.common.gate.IsLessThanRandomGate;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectMutationSupport implements Context.MutationSupport {
    private final IsLessThanRandomGate shouldAddNodeGate;
    private final IsLessThanRandomGate shouldAddConnectionGate;
    private final OutputClassifierFactory<WeightMutationType> weightMutationTypeFactory;
    private final IsLessThanRandomGate shouldDisableExpressedConnectionGate;

    private static IsLessThanRandomGate createIsLessThanGate(final InitializationContext initializationContext, final FloatNumber maximum) {
        return new IsLessThanRandomGate(initializationContext.createDefaultRandomSupport(), initializationContext.provideSingleton(maximum));
    }

    private static OutputClassifierFactory<WeightMutationType> createWeightMutationTypeFactory(final InitializationContext initializationContext, final FloatNumber perturbWeightRate, final FloatNumber replaceWeightRate) {
        RandomFloatFactory randomFloatFactory = new RandomFloatFactory(initializationContext.createDefaultRandomSupport());
        float perturbRate = initializationContext.provideSingleton(perturbWeightRate);
        float replaceRate = initializationContext.provideSingleton(replaceWeightRate);
        float totalRate = (float) Math.ceil(perturbRate + replaceRate);
        ProbabilityClassifier<WeightMutationType> weightMutationTypeClassifier = new ProbabilityClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            weightMutationTypeClassifier.add(perturbRate / totalRate, WeightMutationType.PERTURB);
            weightMutationTypeClassifier.add(replaceRate / totalRate, WeightMutationType.REPLACE);
            weightMutationTypeClassifier.add(1f - (perturbRate + replaceRate) / totalRate, WeightMutationType.NONE);
        } else {
            weightMutationTypeClassifier.add(1f, WeightMutationType.NONE);
        }

        return new OutputClassifierFactory<>(randomFloatFactory, weightMutationTypeClassifier);
    }

    static ContextObjectMutationSupport create(final InitializationContext initializationContext, final MutationSettings mutationSettings) {
        IsLessThanRandomGate shouldAddNodeGate = createIsLessThanGate(initializationContext, mutationSettings.getAddNodeRate());
        IsLessThanRandomGate shouldAddConnectionGate = createIsLessThanGate(initializationContext, mutationSettings.getAddConnectionRate());
        OutputClassifierFactory<WeightMutationType> weightMutationTypeFactory = createWeightMutationTypeFactory(initializationContext, mutationSettings.getPerturbWeightRate(), mutationSettings.getReplaceWeightRate());
        IsLessThanRandomGate shouldDisableExpressedConnectionGate = createIsLessThanGate(initializationContext, mutationSettings.getDisableExpressedConnectionRate());

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

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("mutation.shouldAddNodeGate", shouldAddNodeGate);
        stateGroup.put("mutation.shouldAddConnectionGate", shouldAddConnectionGate);
        stateGroup.put("mutation.weightMutationTypeFactory", weightMutationTypeFactory);
        stateGroup.put("mutation.shouldDisableExpressedConnectionGate", shouldDisableExpressedConnectionGate);
    }

    static ContextObjectMutationSupport create(final SerializableStateGroup stateGroup) {
        IsLessThanRandomGate shouldAddNodeGate = stateGroup.get("mutation.shouldAddNodeGate");
        IsLessThanRandomGate shouldAddConnectionGate = stateGroup.get("mutation.shouldAddConnectionGate");
        OutputClassifierFactory<WeightMutationType> weightMutationTypeFactory = stateGroup.get("mutation.weightMutationTypeFactory");
        IsLessThanRandomGate shouldDisableExpressedConnectionGate = stateGroup.get("mutation.shouldDisableExpressedConnectionGate");

        return new ContextObjectMutationSupport(shouldAddNodeGate, shouldAddConnectionGate, weightMutationTypeFactory, shouldDisableExpressedConnectionGate);
    }
}
