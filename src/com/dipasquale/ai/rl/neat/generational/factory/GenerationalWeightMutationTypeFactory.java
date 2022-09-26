package com.dipasquale.ai.rl.neat.generational.factory;

import com.dipasquale.ai.rl.neat.WeightMutationType;
import com.dipasquale.ai.rl.neat.factory.OutputClassifierFactory;
import com.dipasquale.ai.rl.neat.factory.RandomFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.common.random.RandomSupport;

import java.io.Serial;
import java.io.Serializable;

public final class GenerationalWeightMutationTypeFactory implements GenerationalFactory, ObjectFactory<WeightMutationType>, Serializable {
    @Serial
    private static final long serialVersionUID = -8308983809397371167L;
    private final FloatFactory floatFactory;
    private final GenerationalFloatFactory perturbRateFloatFactory;
    private final GenerationalFloatFactory replaceRateFloatFactory;
    private OutputClassifierFactory<WeightMutationType> weightMutationTypeClassifier;

    private static OutputClassifierFactory<WeightMutationType> createWeightMutationTypeClassifier(final FloatFactory floatFactory, final GenerationalFloatFactory perturbRateFloatFactory, final GenerationalFloatFactory replaceRateFloatFactory) {
        float perturbRate = perturbRateFloatFactory.getValue();
        float replaceRate = replaceRateFloatFactory.getValue();
        float totalRate = (float) Math.ceil(perturbRate + replaceRate);
        ProbabilityClassifier<WeightMutationType> weightMutationTypeClassifier = new ProbabilityClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            weightMutationTypeClassifier.add(perturbRate / totalRate, WeightMutationType.PERTURB);
            weightMutationTypeClassifier.add(replaceRate / totalRate, WeightMutationType.REPLACE);
            weightMutationTypeClassifier.add(1f - (perturbRate + replaceRate) / totalRate, WeightMutationType.NONE);
        } else {
            weightMutationTypeClassifier.add(1f, WeightMutationType.NONE);
        }

        return new OutputClassifierFactory<>(floatFactory, weightMutationTypeClassifier);
    }

    public GenerationalWeightMutationTypeFactory(final FloatFactory floatFactory, final GenerationalFloatFactory perturbRateFloatFactory, final GenerationalFloatFactory replaceRateFloatFactory) {
        this.floatFactory = floatFactory;
        this.perturbRateFloatFactory = perturbRateFloatFactory;
        this.replaceRateFloatFactory = replaceRateFloatFactory;
        this.weightMutationTypeClassifier = createWeightMutationTypeClassifier(floatFactory, perturbRateFloatFactory, replaceRateFloatFactory);
    }

    public GenerationalWeightMutationTypeFactory(final RandomSupport randomSupport, final FloatFactory perturbRateFloatFactory, final FloatFactory replaceRateFloatFactory) {
        this(new RandomFloatFactory(randomSupport), new GenerationalFloatFactory(perturbRateFloatFactory), new GenerationalFloatFactory(replaceRateFloatFactory));
    }

    @Override
    public void reinitialize() {
        perturbRateFloatFactory.reinitialize();
        replaceRateFloatFactory.reinitialize();
        weightMutationTypeClassifier = createWeightMutationTypeClassifier(floatFactory, perturbRateFloatFactory, replaceRateFloatFactory);
    }

    @Override
    public WeightMutationType create() {
        return weightMutationTypeClassifier.create();
    }
}
