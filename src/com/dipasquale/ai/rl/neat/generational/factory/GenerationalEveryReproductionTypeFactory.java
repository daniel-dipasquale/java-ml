package com.dipasquale.ai.rl.neat.generational.factory;

import com.dipasquale.ai.rl.neat.factory.OutputClassifierFactory;
import com.dipasquale.ai.rl.neat.factory.RandomFloatFactory;
import com.dipasquale.ai.rl.neat.speciation.ReproductionType;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenerationalEveryReproductionTypeFactory implements GenerationalFactory, ObjectFactory<ReproductionType>, Serializable {
    @Serial
    private static final long serialVersionUID = -2612736736935232536L;
    private final FloatFactory floatFactory;
    private final GenerationalFloatFactory mateOnlyRateFloatFactory;
    private final GenerationalFloatFactory mutateOnlyRateFloatFactory;
    private OutputClassifierFactory<ReproductionType> reproductionTypeClassifier;

    private static OutputClassifierFactory<ReproductionType> createReproductionTypeClassifier(final FloatFactory floatFactory, final GenerationalFloatFactory mateOnlyRateFloatFactory, final GenerationalFloatFactory mutateOnlyRateFloatFactory) {
        float mateOnlyRate = mateOnlyRateFloatFactory.getValue();
        float mutateOnlyRate = mutateOnlyRateFloatFactory.getValue();
        float totalRate = mateOnlyRate * 2f + mutateOnlyRate * 2f;
        ProbabilityClassifier<ReproductionType> reproductionTypeClassifier = new ProbabilityClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            reproductionTypeClassifier.add(mateOnlyRate / totalRate, ReproductionType.MATE_ONLY);
            reproductionTypeClassifier.add(mutateOnlyRate / totalRate, ReproductionType.MUTATE_ONLY);
            reproductionTypeClassifier.add(1f - (mateOnlyRate + mutateOnlyRate) / totalRate, ReproductionType.MATE_AND_MUTATE);
        } else {
            reproductionTypeClassifier.add(1f, ReproductionType.MATE_AND_MUTATE);
        }

        return new OutputClassifierFactory<>(floatFactory, reproductionTypeClassifier);
    }

    public GenerationalEveryReproductionTypeFactory(final FloatFactory floatFactory, final GenerationalFloatFactory mateOnlyRateFloatFactory, final GenerationalFloatFactory mutateOnlyRateFloatFactory) {
        this(floatFactory, mateOnlyRateFloatFactory, mutateOnlyRateFloatFactory, createReproductionTypeClassifier(floatFactory, mateOnlyRateFloatFactory, mutateOnlyRateFloatFactory));
    }

    public GenerationalEveryReproductionTypeFactory(final RandomSupport randomSupport, final FloatFactory mateOnlyRateFloatFactory, final FloatFactory mutateOnlyRateFloatFactory) {
        this(new RandomFloatFactory(randomSupport), new GenerationalFloatFactory(mateOnlyRateFloatFactory), new GenerationalFloatFactory(mutateOnlyRateFloatFactory));
    }

    @Override
    public void reinitialize() {
        mateOnlyRateFloatFactory.reinitialize();
        mutateOnlyRateFloatFactory.reinitialize();
        reproductionTypeClassifier = createReproductionTypeClassifier(floatFactory, mateOnlyRateFloatFactory, mutateOnlyRateFloatFactory);
    }

    @Override
    public ReproductionType create() {
        return reproductionTypeClassifier.create();
    }
}
