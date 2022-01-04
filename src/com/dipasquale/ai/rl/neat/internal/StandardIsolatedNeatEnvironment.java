package com.dipasquale.ai.rl.neat.internal;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.core.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public final class StandardIsolatedNeatEnvironment implements FitnessFunction<GenomeActivator>, Serializable {
    @Serial
    private static final long serialVersionUID = 2855433963128291511L;
    @Setter
    private transient IsolatedNeatEnvironment environment;
    @Getter
    @Setter
    private transient Exception environmentLoadException;
    private final Map<String, FitnessBucket> fitnessBuckets;

    public StandardIsolatedNeatEnvironment(final IsolatedNeatEnvironment environment, final Map<String, FitnessBucket> fitnessBuckets) {
        this.environment = environment;
        this.environmentLoadException = null;
        this.fitnessBuckets = fitnessBuckets;
    }

    @Override
    public float test(final GenomeActivator genomeActivator) {
        FitnessBucket fitnessBucket = fitnessBuckets.get(genomeActivator.getGenome().getId());
        float fitness = environment.test(genomeActivator);

        return fitnessBucket.incorporate(genomeActivator, fitness);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        try {
            environment = (IsolatedNeatEnvironment) objectInputStream.readObject();
        } catch (Exception e) {
            environmentLoadException = e;
        }

        objectInputStream.defaultReadObject();
    }

    @Serial
    private void writeObject(final ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.writeObject(environment);
        objectOutputStream.defaultWriteObject();
    }
}
