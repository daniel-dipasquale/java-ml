package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.DualModeSequentialIdFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDeque;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeGenomePool implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6680979157037947466L;
    private final DualModeSequentialIdFactory genomeIdFactory;
    private final DualModeDeque<String> disposedGenomeIds;

    public DualModeGenomePool(final boolean concurrent) {
        this(new DualModeSequentialIdFactory(concurrent, "genome"), new DualModeDeque<>(concurrent));
    }

    public String createId() {
        String id = disposedGenomeIds.pollFirst();

        if (id != null) {
            return id;
        }

        return genomeIdFactory.create().toString();
    }

    public void clearIds() {
        genomeIdFactory.reset();
    }

    public Genome createGenesis(final Context context) {
        Genome genome = new Genome(createId());

        context.nodes().setupInitialNodes(genome);
        context.connections().setupInitialConnections(genome);

        return genome;
    }

    public void disposeId(final Genome genome) {
        disposedGenomeIds.add(genome.getId());
    }

    public int getDisposedCount() {
        return disposedGenomeIds.size();
    }

    @Override
    public void switchMode(final boolean concurrent) {
        genomeIdFactory.switchMode(concurrent);
        disposedGenomeIds.switchMode(concurrent);
    }
}