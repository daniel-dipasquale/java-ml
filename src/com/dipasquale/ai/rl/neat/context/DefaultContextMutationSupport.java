package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.common.WeightMutationType;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.MutationSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.dual.profile.provider.IsLessThanRandomGateProviderProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextMutationSupport implements Context.MutationSupport {
    private ObjectProfile<GateProvider> shouldAddNodeProfile;
    private ObjectProfile<GateProvider> shouldAddConnectionProfile;
    private ObjectProfile<ObjectFactory<WeightMutationType>> weightMutationTypeFactoryProfile;
    private ObjectProfile<GateProvider> shouldDisableExpressedConnectionProfile;

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelismSupport, final Pair<RandomSupport> randomSupportPair, final float max) {
        return new IsLessThanRandomGateProviderProfile(parallelismSupport.isEnabled(), randomSupportPair, max);
    }

    private static DefaultReproductionTypeFactoryProfile createWeightMutationTypeFactoryProfile(final ParallelismSupport parallelismSupport, final FloatNumber perturbWeightRate, final FloatNumber replaceWeightRate, final Pair<RandomSupport> randomSupportPair) {
        float perturbRate = perturbWeightRate.createFactoryProfile(parallelismSupport).getObject().create();
        float replaceRate = replaceWeightRate.createFactoryProfile(parallelismSupport).getObject().create();
        float totalRate = (float) Math.ceil(perturbRate + replaceRate);
        OutputClassifier<WeightMutationType> weightMutationTypeClassifier = new OutputClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            weightMutationTypeClassifier.addRangeFor(perturbRate / totalRate, WeightMutationType.PERTURB);
            weightMutationTypeClassifier.addRangeFor(replaceRate / totalRate, WeightMutationType.REPLACE);
        }

        weightMutationTypeClassifier.addRemainingRangeFor(WeightMutationType.NONE);

        return new DefaultReproductionTypeFactoryProfile(parallelismSupport.isEnabled(), randomSupportPair, weightMutationTypeClassifier);
    }

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelismSupport, final Pair<RandomSupport> randomSupportPair, final FloatNumber maximumNumber) {
        float max = maximumNumber.createFactoryProfile(parallelismSupport).getObject().create();

        return createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, max);
    }

    public static DefaultContextMutationSupport create(final ParallelismSupport parallelismSupport, final ObjectProfile<RandomSupport> randomSupportProfile, final MutationSupport mutationSupport) {
        Pair<RandomSupport> randomSupportPair = ObjectProfile.deconstruct(randomSupportProfile);
        ObjectProfile<GateProvider> shouldAddNodeProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, mutationSupport.getAddNodeRate());
        ObjectProfile<GateProvider> shouldAddConnectionProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, mutationSupport.getAddConnectionRate());
        DefaultReproductionTypeFactoryProfile weightMutationTypeFactoryProfile = createWeightMutationTypeFactoryProfile(parallelismSupport, mutationSupport.getPerturbWeightRate(), mutationSupport.getReplaceWeightRate(), randomSupportPair);
        ObjectProfile<GateProvider> shouldDisableExpressedConnectionProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, mutationSupport.getDisableExpressedConnectionRate());

        return new DefaultContextMutationSupport(shouldAddNodeProfile, shouldAddConnectionProfile, weightMutationTypeFactoryProfile, shouldDisableExpressedConnectionProfile);
    }

    @Override
    public boolean shouldAddNode() {
        return shouldAddNodeProfile.getObject().isOn();
    }

    @Override
    public boolean shouldAddConnection() {
        return shouldAddConnectionProfile.getObject().isOn();
    }

    @Override
    public WeightMutationType generateWeightMutationType() {
        return weightMutationTypeFactoryProfile.getObject().create();
    }

    @Override
    public boolean shouldDisableExpressedConnection() {
        return shouldDisableExpressedConnectionProfile.getObject().isOn();
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("mutation.shouldAddNodeProfile", shouldAddNodeProfile);
        stateGroup.put("mutation.shouldAddConnectionProfile", shouldAddConnectionProfile);
        stateGroup.put("mutation.weightMutationTypeFactoryProfile", weightMutationTypeFactoryProfile);
        stateGroup.put("mutation.shouldDisableExpressedConnectionProfile", shouldDisableExpressedConnectionProfile);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        shouldAddNodeProfile = ObjectProfile.switchProfile(stateGroup.get("mutation.shouldAddNodeProfile"), eventLoop != null);
        shouldAddConnectionProfile = ObjectProfile.switchProfile(stateGroup.get("mutation.shouldAddConnectionProfile"), eventLoop != null);
        weightMutationTypeFactoryProfile = ObjectProfile.switchProfile(stateGroup.get("mutation.weightMutationTypeFactoryProfile"), eventLoop != null);
        shouldDisableExpressedConnectionProfile = ObjectProfile.switchProfile(stateGroup.get("mutation.shouldDisableExpressedConnectionProfile"), eventLoop != null);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultWeightMutationTypeFactory implements ObjectFactory<WeightMutationType>, Serializable {
        @Serial
        private static final long serialVersionUID = 8271108235303831278L;
        private final RandomSupport randomSupport;
        private final OutputClassifier<WeightMutationType> weightMutationTypeClassifier;

        @Override
        public WeightMutationType create() {
            float value = randomSupport.next();

            return weightMutationTypeClassifier.resolve(value);
        }
    }

    private static final class DefaultReproductionTypeFactoryProfile extends AbstractObjectProfile<ObjectFactory<WeightMutationType>> {
        @Serial
        private static final long serialVersionUID = -6073304827549187092L;

        private DefaultReproductionTypeFactoryProfile(final boolean concurrent, final Pair<RandomSupport> randomSupportPair, final OutputClassifier<WeightMutationType> weightMutationTypeClassifier) {
            super(concurrent, new DefaultWeightMutationTypeFactory(randomSupportPair.getLeft(), weightMutationTypeClassifier), new DefaultWeightMutationTypeFactory(randomSupportPair.getRight(), weightMutationTypeClassifier));
        }
    }
}
