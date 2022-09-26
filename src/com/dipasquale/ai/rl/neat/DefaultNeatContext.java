package com.dipasquale.ai.rl.neat;

import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PRIVATE)
final class DefaultNeatContext implements NeatContext {
    private final DefaultNeatContextParallelismSupport parallelismSupport;
    private final DefaultNeatContextRandomnessSupport randomnessSupport;
    private final DefaultNeatContextActivationSupport activationSupport;
    private final DefaultNeatContextNodeGeneSupport nodeGeneSupport;
    private final DefaultNeatContextConnectionGeneSupport connectionGeneSupport;
    private final DefaultNeatContextMutationSupport mutationSupport;
    private final DefaultNeatContextCrossOverSupport crossOverSupport;
    private final DefaultNeatContextSpeciationSupport speciationSupport;
    private final DefaultNeatContextMetricsSupport metricsSupport;

    @Override
    public ParallelismSupport getParallelism() {
        return parallelismSupport;
    }

    @Override
    public RandomnessSupport getRandomness() {
        return randomnessSupport;
    }

    @Override
    public ActivationSupport getActivation() {
        return activationSupport;
    }

    @Override
    public NodeGeneSupport getNodeGenes() {
        return nodeGeneSupport;
    }

    @Override
    public ConnectionGeneSupport getConnectionGenes() {
        return connectionGeneSupport;
    }

    @Override
    public MutationSupport getMutation() {
        return mutationSupport;
    }

    @Override
    public CrossOverSupport getCrossOver() {
        return crossOverSupport;
    }

    @Override
    public SpeciationSupport getSpeciation() {
        return speciationSupport;
    }

    @Override
    public MetricsSupport getMetrics() {
        return metricsSupport;
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        randomnessSupport.save(stateGroup);
        activationSupport.save(stateGroup);
        nodeGeneSupport.save(stateGroup);
        connectionGeneSupport.save(stateGroup);
        mutationSupport.save(stateGroup);
        crossOverSupport.save(stateGroup);
        speciationSupport.save(stateGroup);
        metricsSupport.save(stateGroup);
        stateGroup.writeTo(outputStream);
    }

    static DefaultNeatContext create(final ObjectInputStream objectInputStream, final PretrainedSupport pretrainedSupport)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.readFrom(objectInputStream);

        return DefaultNeatContext.builder()
                .parallelismSupport(DefaultNeatContextParallelismSupport.create(pretrainedSupport.getEventLoop()))
                .randomnessSupport(DefaultNeatContextRandomnessSupport.create(stateGroup))
                .activationSupport(DefaultNeatContextActivationSupport.create(stateGroup, pretrainedSupport.getFitnessFunction()))
                .nodeGeneSupport(DefaultNeatContextNodeGeneSupport.create(stateGroup))
                .connectionGeneSupport(DefaultNeatContextConnectionGeneSupport.create(stateGroup))
                .mutationSupport(DefaultNeatContextMutationSupport.create(stateGroup))
                .crossOverSupport(DefaultNeatContextCrossOverSupport.create(stateGroup))
                .speciationSupport(DefaultNeatContextSpeciationSupport.create(stateGroup, pretrainedSupport.getEventLoop()))
                .metricsSupport(DefaultNeatContextMetricsSupport.create(stateGroup, pretrainedSupport.getEventLoop()))
                .build();
    }
}
