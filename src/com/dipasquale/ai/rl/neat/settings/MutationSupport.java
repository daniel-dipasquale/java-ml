package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.common.WeightMutationType;
import com.dipasquale.ai.rl.neat.context.DefaultContextMutationSupport;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.dual.profile.provider.IsLessThanRandomGateProviderProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class MutationSupport {
    @Builder.Default
    private final FloatNumber addNodeRate = FloatNumber.literal(0.03f);
    @Builder.Default
    private final FloatNumber addConnectionRate = FloatNumber.literal(0.06f);
    @Builder.Default
    private final FloatNumber perturbWeightRate = FloatNumber.literal(0.75f);
    @Builder.Default
    private final FloatNumber replaceWeightRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber disableExpressedConnectionRate = FloatNumber.literal(0.015f);

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelismSupport, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final float max) {
        return new IsLessThanRandomGateProviderProfile(parallelismSupport.isEnabled(), randomSupportPair, max);
    }

    private DefaultReproductionTypeFactoryProfile createWeightMutationTypeFactoryProfile(final ParallelismSupport parallelismSupport, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair) {
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

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelismSupport, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final FloatNumber maximumNumber) {
        float max = maximumNumber.createFactoryProfile(parallelismSupport).getObject().create();

        return createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, max);
    }

    DefaultContextMutationSupport create(final ParallelismSupport parallelismSupport, final RandomSupport randomSupport) {
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> randomSupportProfile = randomSupport.createFloatRandomSupportProfile(parallelismSupport);
        Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair = ObjectProfile.deconstruct(randomSupportProfile);
        ObjectProfile<GateProvider> shouldAddNodeProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, addNodeRate);
        ObjectProfile<GateProvider> shouldAddConnectionProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, addConnectionRate);
        DefaultReproductionTypeFactoryProfile weightMutationTypeFactoryProfile = createWeightMutationTypeFactoryProfile(parallelismSupport, randomSupportPair);
        ObjectProfile<GateProvider> shouldDisableExpressedConnectionProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, disableExpressedConnectionRate);

        return new DefaultContextMutationSupport(shouldAddNodeProfile, shouldAddConnectionProfile, weightMutationTypeFactoryProfile, shouldDisableExpressedConnectionProfile);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultWeightMutationTypeFactory implements ObjectFactory<WeightMutationType>, Serializable {
        @Serial
        private static final long serialVersionUID = 8271108235303831278L;
        private final com.dipasquale.common.random.float1.RandomSupport randomSupport;
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

        private DefaultReproductionTypeFactoryProfile(final boolean concurrent,
                                                      final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair,
                                                      final OutputClassifier<WeightMutationType> weightMutationTypeClassifier) {
            super(concurrent, new DefaultWeightMutationTypeFactory(randomSupportPair.getLeft(), weightMutationTypeClassifier), new DefaultWeightMutationTypeFactory(randomSupportPair.getRight(), weightMutationTypeClassifier));
        }
    }
}
