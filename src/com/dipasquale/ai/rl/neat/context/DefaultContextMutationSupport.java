package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.common.WeightMutationType;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.MutationSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeOutputClassifierFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomFloatFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.provider.DualModeIsLessThanRandomGateProvider;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextMutationSupport implements Context.MutationSupport {
    private DualModeIsLessThanRandomGateProvider shouldAddNodeGateProvider;
    private DualModeIsLessThanRandomGateProvider shouldAddConnectionGateProvider;
    private DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> weightMutationTypeFactory;
    private DualModeIsLessThanRandomGateProvider shouldDisableExpressedConnectionGateProvider;

    private static DualModeIsLessThanRandomGateProvider createIsLessThanGateProvider(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final FloatNumber maximumNumber) {
        float max = maximumNumber.getSingletonValue(parallelismSupport, randomSupports);

        return new DualModeIsLessThanRandomGateProvider(randomSupport, max);
    }

    private static DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> createWeightMutationTypeFactoryProfile(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final FloatNumber perturbWeightRate, final FloatNumber replaceWeightRate) {
        float perturbRate = perturbWeightRate.getSingletonValue(parallelismSupport, randomSupports);
        float replaceRate = replaceWeightRate.getSingletonValue(parallelismSupport, randomSupports);
        float totalRate = (float) Math.ceil(perturbRate + replaceRate);
        OutputClassifier<WeightMutationType> weightMutationTypeClassifier = new OutputClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            weightMutationTypeClassifier.addRangeFor(perturbRate / totalRate, WeightMutationType.PERTURB);
            weightMutationTypeClassifier.addRangeFor(replaceRate / totalRate, WeightMutationType.REPLACE);
        }

        weightMutationTypeClassifier.addRemainingRangeFor(WeightMutationType.NONE);

        return new DualModeOutputClassifierFactory<>(new DualModeRandomFloatFactory(randomSupport), weightMutationTypeClassifier);
    }

    public static DefaultContextMutationSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final MutationSupport mutationSupport) {
        DualModeIsLessThanRandomGateProvider shouldAddNodeGateProvider = createIsLessThanGateProvider(parallelismSupport, randomSupports, randomSupport, mutationSupport.getAddNodeRate());
        DualModeIsLessThanRandomGateProvider shouldAddConnectionGateProvider = createIsLessThanGateProvider(parallelismSupport, randomSupports, randomSupport, mutationSupport.getAddConnectionRate());
        DualModeOutputClassifierFactory<DualModeRandomFloatFactory, WeightMutationType> weightMutationTypeFactoryProfile = createWeightMutationTypeFactoryProfile(parallelismSupport, randomSupports, randomSupport, mutationSupport.getPerturbWeightRate(), mutationSupport.getReplaceWeightRate());
        DualModeIsLessThanRandomGateProvider shouldDisableExpressedConnectionGateProvider = createIsLessThanGateProvider(parallelismSupport, randomSupports, randomSupport, mutationSupport.getDisableExpressedConnectionRate());

        return new DefaultContextMutationSupport(shouldAddNodeGateProvider, shouldAddConnectionGateProvider, weightMutationTypeFactoryProfile, shouldDisableExpressedConnectionGateProvider);
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
        shouldAddNodeGateProvider = DualModeObject.activateMode(stateGroup.get("mutation.shouldAddNodeGateProvider"), eventLoop == null ? 0 : eventLoop.getConcurrencyLevel());
        shouldAddConnectionGateProvider = DualModeObject.activateMode(stateGroup.get("mutation.shouldAddConnectionGateProvider"), eventLoop == null ? 0 : eventLoop.getConcurrencyLevel());
        weightMutationTypeFactory = DualModeObject.activateMode(stateGroup.get("mutation.weightMutationTypeFactory"), eventLoop == null ? 0 : eventLoop.getConcurrencyLevel());
        shouldDisableExpressedConnectionGateProvider = DualModeObject.activateMode(stateGroup.get("mutation.shouldDisableExpressedConnectionGateProvider"), eventLoop == null ? 0 : eventLoop.getConcurrencyLevel());
    }
}
