package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.WeightPerturber;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.ai.rl.neat.speciation.PopulationHistoricalMarkings;
import com.dipasquale.common.FloatFactory;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ContextDefaultConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private boolean multipleRecurrentCyclesAllowed;
    private FloatFactory weightFactory;
    private WeightPerturber weightPerturber;
    private GenomeGenesisConnector genomeGenesisConnector;

    @Override
    public boolean multipleRecurrentCyclesAllowed() {
        return multipleRecurrentCyclesAllowed;
    }

    @Override
    public float nextWeight() {
        return weightFactory.create();
    }

    @Override
    public float perturbWeight(final float weight) {
        return weightPerturber.perturb(weight);
    }

    @Override
    public void setupInitialConnections(final GenomeDefault genome, final PopulationHistoricalMarkings historicalMarkings) {
        genomeGenesisConnector.setupConnections(genome, historicalMarkings);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("connections.multipleRecurrentCyclesAllowed", multipleRecurrentCyclesAllowed);
        state.put("connections.weightFactory", weightFactory);
        state.put("connections.weightPerturber", weightPerturber);
        state.put("connections.genomeGenesisConnector", genomeGenesisConnector);
    }

    public void load(final SerializableInteroperableStateMap state) {
        multipleRecurrentCyclesAllowed = state.get("connections.multipleRecurrentCyclesAllowed");
        weightFactory = state.get("connections.weightFactory");
        weightPerturber = state.get("connections.weightPerturber");
        genomeGenesisConnector = state.get("connections.genomeGenesisConnector");
    }
}
