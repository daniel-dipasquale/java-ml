package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.serialization.SerializableStateGroup;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@RequiredArgsConstructor
public final class DefaultContext implements Context {
    private final DefaultContextGeneralSupport generalSupport;
    private final DefaultContextNodeGeneSupport nodeGeneSupport;
    private final DefaultContextConnectionGeneSupport connectionGeneSupport;
    private final DefaultContextActivationSupport activationSupport;
    private final DefaultContextParallelismSupport parallelismSupport;
    private final DefaultContextRandomSupport randomSupport;
    private final DefaultContextMutationSupport mutationSupport;
    private final DefaultContextCrossOverSupport crossOverSupport;
    private final DefaultContextSpeciationSupport speciationSupport;

    @Override
    public GeneralSupport general() {
        return generalSupport;
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
    public ParallelismSupport parallelism() {
        return parallelismSupport;
    }

    @Override
    public RandomSupport random() {
        return randomSupport;
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
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup state = new SerializableStateGroup();

        generalSupport.save(state);
        nodeGeneSupport.save(state);
        connectionGeneSupport.save(state);
        activationSupport.save(state);
        randomSupport.save(state);
        mutationSupport.save(state);
        crossOverSupport.save(state);
        speciationSupport.save(state);
        state.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream, final StateOverrideSupport override)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup state = new SerializableStateGroup();

        state.readFrom(inputStream);
        generalSupport.load(state);
        nodeGeneSupport.load(state, override.eventLoop());
        connectionGeneSupport.load(state, override.eventLoop());
        activationSupport.load(state, override.eventLoop(), override.fitnessFunction());
        parallelismSupport.load(override.eventLoop());
        randomSupport.load(state, override.eventLoop());
        mutationSupport.load(state, override.eventLoop());
        crossOverSupport.load(state, override.eventLoop());
        speciationSupport.load(state, override.eventLoop());
    }
}
