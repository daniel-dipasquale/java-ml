package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextObject implements Context {
    private final ContextObjectGeneralSupport generalSupport;
    private final ContextObjectParallelismSupport parallelismSupport;
    private final ContextObjectRandomSupport randomSupport;
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
    public RandomSupport random() {
        return randomSupport;
    }

    @Override
    public NodeGeneSupport nodes() {
        return nodeGeneSupport;
    }

    @Override
    public ConnectionGeneSupport connections() {
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
        randomSupport.save(stateGroup);
        nodeGeneSupport.save(stateGroup);
        connectionGeneSupport.save(stateGroup);
        activationSupport.save(stateGroup);
        mutationSupport.save(stateGroup);
        crossOverSupport.save(stateGroup);
        speciationSupport.save(stateGroup);
        metricsSupport.save(stateGroup);
        stateGroup.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream, final StateOverrideSupport override)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.readFrom(inputStream);
        generalSupport.load(stateGroup);
        parallelismSupport.load(override.eventLoop());
        randomSupport.load(stateGroup, override.eventLoop());
        nodeGeneSupport.load(stateGroup, override.eventLoop());
        connectionGeneSupport.load(stateGroup, override.eventLoop());
        activationSupport.load(stateGroup, override.eventLoop(), override.fitnessFunction());
        mutationSupport.load(stateGroup, override.eventLoop());
        crossOverSupport.load(stateGroup, override.eventLoop());
        speciationSupport.load(stateGroup, override.eventLoop());
        metricsSupport.load(stateGroup, override.eventLoop());
    }
}
