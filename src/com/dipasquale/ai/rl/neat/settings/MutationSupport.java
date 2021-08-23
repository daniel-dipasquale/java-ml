package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.context.DefaultContextMutationSupport;
import com.dipasquale.ai.rl.neat.genotype.WeightMutationType;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.profile.AbstractObjectProfile;
import com.dipasquale.common.profile.ObjectProfile;
import com.dipasquale.common.profile.provider.IsLessThanRandomGateProviderProfile;
import com.dipasquale.common.provider.GateProvider;
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
    private final FloatNumber addNodeMutationRate = FloatNumber.literal(0.1f);
    @Builder.Default
    private final FloatNumber addConnectionMutationRate = FloatNumber.literal(0.2f);
    @Builder.Default
    private final FloatNumber perturbWeightRate = FloatNumber.literal(0.75f);
    @Builder.Default
    private final FloatNumber replaceWeightRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber disableExpressedRate = FloatNumber.literal(0.05f);

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelism, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final float max) {
        return new IsLessThanRandomGateProviderProfile(parallelism.isEnabled(), randomSupportPair, max);
    }

    private DefaultReproductionTypeFactoryProfile createWeightMutationTypeFactoryProfile(final ParallelismSupport parallelism, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair) {
        float perturbRate = perturbWeightRate.createFactoryProfile(parallelism).getObject().create();
        float replaceRate = replaceWeightRate.createFactoryProfile(parallelism).getObject().create();
        float totalRate = (float) Math.ceil(perturbRate + replaceRate);
        OutputClassifier<WeightMutationType> weightMutationTypeClassifier = new OutputClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            weightMutationTypeClassifier.addUpUntil(WeightMutationType.PERTURB, perturbRate / totalRate);
            weightMutationTypeClassifier.addUpUntil(WeightMutationType.REPLACE, replaceRate / totalRate);
        }

        weightMutationTypeClassifier.addOtherwiseRoundedUp(WeightMutationType.NONE);

        return new DefaultReproductionTypeFactoryProfile(parallelism.isEnabled(), randomSupportPair, weightMutationTypeClassifier);
    }

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelism, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final FloatNumber maximumNumber) {
        float max = maximumNumber.createFactoryProfile(parallelism).getObject().create();

        return createIsLessThanProviderProfile(parallelism, randomSupportPair, max);
    }

    DefaultContextMutationSupport create(final ParallelismSupport parallelism, final RandomSupport random) {
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> randomSupportProfile = random.createIsLessThanProfile(parallelism);
        Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair = ObjectProfile.deconstruct(randomSupportProfile);
        ObjectProfile<GateProvider> shouldAddNodeMutationProfile = createIsLessThanProviderProfile(parallelism, randomSupportPair, addNodeMutationRate);
        ObjectProfile<GateProvider> shouldAddConnectionMutationProfile = createIsLessThanProviderProfile(parallelism, randomSupportPair, addConnectionMutationRate);
        DefaultReproductionTypeFactoryProfile weightMutationTypeFactoryProfile = createWeightMutationTypeFactoryProfile(parallelism, randomSupportPair);
        ObjectProfile<GateProvider> shouldDisableExpressedProfile = createIsLessThanProviderProfile(parallelism, randomSupportPair, disableExpressedRate);

        return new DefaultContextMutationSupport(shouldAddNodeMutationProfile, shouldAddConnectionMutationProfile, weightMutationTypeFactoryProfile, shouldDisableExpressedProfile);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultWeightMutationTypeFactory implements ObjectFactory<WeightMutationType>, Serializable {
        @Serial
        private static final long serialVersionUID = 8271108235303831278L;
        private final com.dipasquale.common.random.float1.RandomSupport randomSupport;
        private final OutputClassifier<WeightMutationType> weightMutationTypeClassifier;

        @Override
        public WeightMutationType create() {
            float random = randomSupport.next();

            return weightMutationTypeClassifier.resolve(random);
        }
    }

    private static final class DefaultReproductionTypeFactoryProfile extends AbstractObjectProfile<ObjectFactory<WeightMutationType>> {
        @Serial
        private static final long serialVersionUID = -6073304827549187092L;

        private DefaultReproductionTypeFactoryProfile(final boolean isOn,
                                                      final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair,
                                                      final OutputClassifier<WeightMutationType> weightMutationTypeClassifier) {
            super(isOn, new DefaultWeightMutationTypeFactory(randomSupportPair.getLeft(), weightMutationTypeClassifier), new DefaultWeightMutationTypeFactory(randomSupportPair.getRight(), weightMutationTypeClassifier));
        }
    }
}
