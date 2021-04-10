package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.WeightPerturber;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.ai.rl.neat.speciation.PopulationHistoricalMarkings;
import com.dipasquale.common.FloatFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
    public void setupInitialConnection(final GenomeDefault genome, final PopulationHistoricalMarkings historicalMarkings) {
        genomeGenesisConnector.connect(genome, historicalMarkings);
    }

    public void save(final ContextDefaultStateMap state) {
        state.put("connections.genomeGenesisConnector", genomeGenesisConnector);
        state.put("connections.multipleRecurrentCyclesAllowed", multipleRecurrentCyclesAllowed);
        state.put("connections.weightFactory", weightFactory);
        state.put("connections.weightPerturber", weightPerturber);
    }

    public void load(final ContextDefaultStateMap state) {
        genomeGenesisConnector = state.get("connections.genomeGenesisConnector");
        multipleRecurrentCyclesAllowed = state.get("connections.multipleRecurrentCyclesAllowed");
        weightFactory = state.get("connections.weightFactory");
        weightPerturber = state.get("connections.weightPerturber");
    }
}
