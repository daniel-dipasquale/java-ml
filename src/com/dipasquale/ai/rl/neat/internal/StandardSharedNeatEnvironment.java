package com.dipasquale.ai.rl.neat.internal;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.core.SharedGenomeActivator;
import com.dipasquale.ai.rl.neat.core.SharedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeFloatValue;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class StandardSharedNeatEnvironment implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -6417656439061002573L;
    private final ConcurrencyLevelState concurrencyLevelState;
    @Setter
    private transient SharedNeatEnvironment environment;
    @Getter
    @Setter
    private transient Exception environmentLoadException;
    private final Map<String, FitnessBucket> fitnessBuckets;

    public StandardSharedNeatEnvironment(final int concurrencyLevel, final SharedNeatEnvironment environment, final Map<String, FitnessBucket> fitnessBuckets) {
        this.concurrencyLevelState = new ConcurrencyLevelState(concurrencyLevel);
        this.environment = environment;
        this.environmentLoadException = null;
        this.fitnessBuckets = fitnessBuckets;
    }

    private float incorporateFitness(final GenomeActivator genomeActivator, final float fitness) {
        FitnessBucket fitnessBucket = fitnessBuckets.get(genomeActivator.getGenome().getId());

        return fitnessBucket.incorporate(genomeActivator, fitness);
    }

    public List<Float> test(final Context context, final List<GenomeActivator> genomeActivators) {
        List<GenomeActivator> genomeActivatorsFixed = new ArrayList<>();
        Map<GenomeActivator, DualModeFloatValue> fitnessValues = new IdentityHashMap<>();
        int concurrencyLevel = concurrencyLevelState.getCurrent();

        for (GenomeActivator genomeActivator : genomeActivators) {
            genomeActivatorsFixed.add(genomeActivator);
            fitnessValues.put(genomeActivator, new DualModeFloatValue(concurrencyLevel));
        }

        SharedGenomeActivator sharedGenomeActivator = new SharedGenomeActivator(context, genomeActivatorsFixed, fitnessValues);

        environment.test(sharedGenomeActivator);

        return genomeActivators.stream()
                .map(genomeActivator -> incorporateFitness(genomeActivator, fitnessValues.get(genomeActivator).current()))
                .collect(Collectors.toList());
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        try {
            environment = (SharedNeatEnvironment) objectInputStream.readObject();
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
