package com.dipasquale.ai.rl.neat.generational.factory;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.common.factory.FloatFactory;

import java.io.Serial;
import java.io.Serializable;

public final class GenerationalGenomeCompatibilityCalculatorFactory implements GenerationalFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 3337312784044042710L;
    private final GenerationalFloatFactory excessCoefficientFactory;
    private final GenerationalFloatFactory disjointCoefficientFactory;
    private final GenerationalFloatFactory weightDifferenceCoefficientFactory;
    private final GenomeCompatibilityCalculator genomeCompatibilityCalculator;

    public GenerationalGenomeCompatibilityCalculatorFactory(final GenerationalFloatFactory excessCoefficientFactory, final GenerationalFloatFactory disjointCoefficientFactory, final GenerationalFloatFactory weightDifferenceCoefficientFactory) {
        this.excessCoefficientFactory = excessCoefficientFactory;
        this.disjointCoefficientFactory = disjointCoefficientFactory;
        this.weightDifferenceCoefficientFactory = weightDifferenceCoefficientFactory;
        this.genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(excessCoefficientFactory::getValue, disjointCoefficientFactory::getValue, weightDifferenceCoefficientFactory::getValue);
    }

    public GenerationalGenomeCompatibilityCalculatorFactory(final FloatFactory excessCoefficientFactory, final FloatFactory disjointCoefficientFactory, final FloatFactory weightDifferenceCoefficientFactory) {
        this(new GenerationalFloatFactory(excessCoefficientFactory), new GenerationalFloatFactory(disjointCoefficientFactory), new GenerationalFloatFactory(weightDifferenceCoefficientFactory));
    }

    public float calculateCompatibility(final Genome genome1, final Genome genome2) {
        return genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);
    }

    @Override
    public void reinitialize() {
        excessCoefficientFactory.reinitialize();
        disjointCoefficientFactory.reinitialize();
        weightDifferenceCoefficientFactory.reinitialize();
    }
}
