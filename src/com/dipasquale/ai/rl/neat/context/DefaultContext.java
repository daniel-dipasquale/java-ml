package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
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
    private final DefaultContextMetricSupport metricSupport;

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
    public MetricSupport metrics() {
        return metricSupport;
    }

    public static <TKey, TValue> DualModeMap<TKey, TValue> loadMap(final DualModeMap<TKey, TValue> map, final IterableEventLoop eventLoop) {
        DualModeMap<TKey, TValue> mapFixed = DualModeObject.switchMode(map, eventLoop != null);

        if (eventLoop == null) {
            return new DualModeMap<>(false, 1, mapFixed);
        }

        return new DualModeMap<>(true, eventLoop.getConcurrencyLevel(), mapFixed);
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        generalSupport.save(stateGroup);
        nodeGeneSupport.save(stateGroup);
        connectionGeneSupport.save(stateGroup);
        activationSupport.save(stateGroup);
        randomSupport.save(stateGroup);
        mutationSupport.save(stateGroup);
        crossOverSupport.save(stateGroup);
        speciationSupport.save(stateGroup);
        metricSupport.save(stateGroup);
        stateGroup.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream, final StateOverrideSupport override)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.readFrom(inputStream);
        generalSupport.load(stateGroup);
        nodeGeneSupport.load(stateGroup, override.eventLoop());
        connectionGeneSupport.load(stateGroup, override.eventLoop());
        activationSupport.load(stateGroup, override.eventLoop(), override.fitnessFunction());
        parallelismSupport.load(override.eventLoop());
        randomSupport.load(stateGroup, override.eventLoop());
        mutationSupport.load(stateGroup, override.eventLoop());
        crossOverSupport.load(stateGroup, override.eventLoop());
        speciationSupport.load(stateGroup, override.eventLoop());
        metricSupport.load(stateGroup, override.eventLoop());
    }
}
