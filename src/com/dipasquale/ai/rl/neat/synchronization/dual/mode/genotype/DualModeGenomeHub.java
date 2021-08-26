package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.DualModeSequentialIdFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDeque;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeGenomeHub implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6680979157037947466L;
    private final DualModeSequentialIdFactory genomeIdFactory;
    private final DualModeDeque<String> genomeIdsKilled;

    public DualModeGenomeHub(final boolean concurrent) {
        this(new DualModeSequentialIdFactory(concurrent, "genome"), new DualModeDeque<>(concurrent));
    }

    public String createGenomeId() {
        String id = genomeIdsKilled.pollFirst();

        if (id != null) {
            return id;
        }

        return genomeIdFactory.create().toString();
    }

    public DefaultGenome createGenome(final Context context) {
        DefaultGenome genome = new DefaultGenome(createGenomeId());

        context.nodes().setupInitialNodes(genome);
        context.connections().setupInitialConnections(genome);
        // TODO: register nodes here

        return genome;
    }

    public void markToKill(final DefaultGenome genome) {
        genomeIdsKilled.add(genome.getId());
        // TODO: deregister nodes here
    }

    public int getGenomeKilledCount() {
        return genomeIdsKilled.size();
    }

    @Override
    public void switchMode(final boolean concurrent) {
        genomeIdFactory.switchMode(concurrent);
        genomeIdsKilled.switchMode(concurrent);
    }
}
