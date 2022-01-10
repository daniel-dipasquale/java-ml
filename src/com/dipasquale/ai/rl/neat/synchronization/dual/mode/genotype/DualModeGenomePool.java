package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.core.Context;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.DualModeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.IdType;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDeque;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDequeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeGenomePool implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6680979157037947466L;
    private final DualModeIdFactory genomeIdFactory;
    private final DualModeDeque<String, DualModeDequeFactory> disposedGenomeIds;

    public DualModeGenomePool(final DualModeDequeFactory dequeFactory) {
        this(new DualModeIdFactory(dequeFactory.concurrencyLevel(), IdType.GENOME), new DualModeDeque<>(dequeFactory));
    }

    public String createId() {
        String id = disposedGenomeIds.pollFirst();

        if (id != null) {
            return id;
        }

        return genomeIdFactory.create().toString();
    }

    public void clearIds() {
        disposedGenomeIds.clear();
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
    public int concurrencyLevel() {
        return disposedGenomeIds.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        genomeIdFactory.activateMode(concurrencyLevel);
        disposedGenomeIds.activateMode(concurrencyLevel);
    }
}
