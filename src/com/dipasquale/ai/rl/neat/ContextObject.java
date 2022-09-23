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
final class ContextObject implements Context {
    private final ContextObjectGeneralSupport generalSupport;
    private final ContextObjectParallelismSupport parallelismSupport;
    private final ContextObjectRandomnessSupport randomnessSupport;
    private final ContextObjectNodeGeneSupport nodeGeneSupport;
    private final ContextObjectConnectionGeneSupport connectionGeneSupport;
    private final ContextObjectActivationSupport activationSupport;
    private final ContextObjectMutationSupport mutationSupport;
    private final ContextObjectCrossOverSupport crossOverSupport;
    private final ContextObjectSpeciationSupport speciationSupport;
    private final ContextObjectMetricsSupport metricsSupport;

    @Override
    public GeneralSupport general() {
        return generalSupport;
    }

    @Override
    public ParallelismSupport parallelism() {
        return parallelismSupport;
    }

    @Override
    public RandomnessSupport randomness() {
        return randomnessSupport;
    }

    @Override
    public NodeGeneSupport nodeGenes() {
        return nodeGeneSupport;
    }

    @Override
    public ConnectionGeneSupport connectionGenes() {
        return connectionGeneSupport;
    }

    @Override
    public ActivationSupport activation() {
        return activationSupport;
    }

    @Override
    public MutationSupport mutation() {
        return mutationSupport;
    }

    @Override
    public CrossOverSupport crossOver() {
        return crossOverSupport;
    }

    @Override
    public SpeciationSupport speciation() {
        return speciationSupport;
    }

    @Override
    public MetricsSupport metrics() {
        return metricsSupport;
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        generalSupport.save(stateGroup);
        randomnessSupport.save(stateGroup);
        nodeGeneSupport.save(stateGroup);
        connectionGeneSupport.save(stateGroup);
        activationSupport.save(stateGroup);
        mutationSupport.save(stateGroup);
        crossOverSupport.save(stateGroup);
        speciationSupport.save(stateGroup);
        metricsSupport.save(stateGroup);
        stateGroup.writeTo(outputStream);
    }

    static ContextObject create(final ObjectInputStream objectInputStream, final LoadSupport loadSupport)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.readFrom(objectInputStream);

        return ContextObject.builder()
                .generalSupport(ContextObjectGeneralSupport.create(stateGroup))
                .parallelismSupport(ContextObjectParallelismSupport.create(loadSupport.eventLoop()))
                .randomnessSupport(ContextObjectRandomnessSupport.create(stateGroup))
                .nodeGeneSupport(ContextObjectNodeGeneSupport.create(stateGroup))
                .connectionGeneSupport(ContextObjectConnectionGeneSupport.create(stateGroup))
                .activationSupport(ContextObjectActivationSupport.create(stateGroup, loadSupport.fitnessFunction()))
                .mutationSupport(ContextObjectMutationSupport.create(stateGroup))
                .crossOverSupport(ContextObjectCrossOverSupport.create(stateGroup))
                .speciationSupport(ContextObjectSpeciationSupport.create(stateGroup, loadSupport.eventLoop()))
                .metricsSupport(ContextObjectMetricsSupport.create(stateGroup, loadSupport.eventLoop()))
                .build();
    }
}
