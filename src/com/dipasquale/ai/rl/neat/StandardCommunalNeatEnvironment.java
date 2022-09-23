package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.fitness.FitnessBucket;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.common.FloatValue;
import com.dipasquale.data.structure.collection.IterableArray;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class StandardCommunalNeatEnvironment implements Serializable {
    @Serial
    private static final long serialVersionUID = -6417656439061002573L;
    @Setter
    private transient CommunalNeatEnvironment environment;
    @Getter
    @Setter
    private transient Exception environmentLoadException;
    private final IterableArray<FitnessBucket> fitnessBuckets;

    StandardCommunalNeatEnvironment(final CommunalNeatEnvironment environment, final IterableArray<FitnessBucket> fitnessBuckets) {
        this.environment = environment;
        this.environmentLoadException = null;
        this.fitnessBuckets = fitnessBuckets;
    }

    private float incorporateFitness(final GenomeActivator genomeActivator, final IterableArray<FloatValue> fitnessValues) {
        int genomeId = genomeActivator.getGenome().getId();
        FitnessBucket fitnessBucket = fitnessBuckets.get(genomeId);
        float fitness = fitnessValues.get(genomeId).current();

        return fitnessBucket.incorporate(genomeActivator, fitness);
    }

    public List<Float> test(final Context context, final List<GenomeActivator> genomeActivators) {
        int size = genomeActivators.size();
        IterableArray<FloatValue> fitnessValues = new IterableArray<>(size);
        Context.ParallelismSupport parallelismSupport = context.parallelism();

        for (int genomeId = 0; genomeId < size; genomeId++) {
            fitnessValues.put(genomeId, parallelismSupport.createFloatValue(0f));
        }

        CommunalGenomeActivator communalGenomeActivator = new CommunalGenomeActivator(context, Collections.unmodifiableList(genomeActivators), fitnessValues);

        environment.test(communalGenomeActivator);

        return genomeActivators.stream()
                .map(genomeActivator -> incorporateFitness(genomeActivator, fitnessValues))
                .collect(Collectors.toList());
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        try {
            environment = (CommunalNeatEnvironment) objectInputStream.readObject();
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
