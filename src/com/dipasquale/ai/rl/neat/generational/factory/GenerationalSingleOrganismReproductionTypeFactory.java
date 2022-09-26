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
public final class GenerationalSingleOrganismReproductionTypeFactory implements GenerationalFactory, ObjectFactory<ReproductionType>, Serializable {
    @Serial
    private static final long serialVersionUID = 353734851878199643L;
    private final FloatFactory floatFactory;
    private final GenerationalFloatFactory mutateRateFloatFactory;
    private OutputClassifierFactory<ReproductionType> reproductionTypeClassifier;

    private static OutputClassifierFactory<ReproductionType> createReproductionTypeClassifier(final FloatFactory floatFactory, final float mutateOnlyRate) {
        ProbabilityClassifier<ReproductionType> reproductionTypeClassifier = new ProbabilityClassifier<>();

        if (Float.compare(mutateOnlyRate, 0f) > 0) {
            reproductionTypeClassifier.add(1f, ReproductionType.MUTATE_ONLY);
        } else {
            reproductionTypeClassifier.add(1f, ReproductionType.CLONE);
        }

        return new OutputClassifierFactory<>(null, reproductionTypeClassifier);
    }

    public GenerationalSingleOrganismReproductionTypeFactory(final FloatFactory floatFactory, final GenerationalFloatFactory mutateRateFloatFactory) {
        this(floatFactory, mutateRateFloatFactory, createReproductionTypeClassifier(floatFactory, mutateRateFloatFactory.getValue()));
    }

    public GenerationalSingleOrganismReproductionTypeFactory(final RandomSupport randomSupport, final FloatFactory mutateRateFloatFactory) {
        this(new RandomFloatFactory(randomSupport), new GenerationalFloatFactory(mutateRateFloatFactory));
    }

    @Override
    public void reinitialize() {
        mutateRateFloatFactory.reinitialize();
        reproductionTypeClassifier = createReproductionTypeClassifier(floatFactory, mutateRateFloatFactory.getValue());
    }

    @Override
    public ReproductionType create() {
        return reproductionTypeClassifier.create();
    }
}
