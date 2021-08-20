package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.ai.rl.neat.speciation.core.DefaultGenomeHistoricalMarkings;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private DefaultContextConnectionGeneParameters params;
    private ObjectSwitcher<FloatFactory> weightFactory;
    private ObjectSwitcher<WeightPerturber> weightPerturber;
    private GenomeGenesisConnector genomeGenesisConnector;

    @Override
    public Context.ConnectionGeneParameters params() {
        return params;
    }

    @Override
    public float nextWeight() {
        return weightFactory.getObject().create();
    }

    @Override
    public float perturbWeight(final float weight) {
        return weightPerturber.getObject().perturb(weight);
    }

    @Override
    public void setupInitialConnections(final DefaultGenome genome, final DefaultGenomeHistoricalMarkings historicalMarkings) {
        genomeGenesisConnector.setupConnections(genome, historicalMarkings);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("connections.params", params);
        state.put("connections.weightFactory", weightFactory);
        state.put("connections.weightPerturber", weightPerturber);
        state.put("connections.genomeGenesisConnector", genomeGenesisConnector);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        params = state.get("connections.params");
        weightFactory = ObjectSwitcher.switchObject(state.get("connections.weightFactory"), eventLoop != null);
        weightPerturber = ObjectSwitcher.switchObject(state.get("connections.weightPerturber"), eventLoop != null);
        genomeGenesisConnector = state.get("connections.genomeGenesisConnector");
    }
}
