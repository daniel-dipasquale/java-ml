package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.IntegerIdFactory;
import com.dipasquale.ai.rl.neat.Context;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

@RequiredArgsConstructor
public final class GenomePool implements Serializable {
    @Serial
    private static final long serialVersionUID = 6680979157037947466L;
    private final IntegerIdFactory genomeIdFactory = new IntegerIdFactory();
    private final Deque<Integer> disposedGenomeIds = new LinkedList<>();

    public int createGenomeId() {
        Integer id = disposedGenomeIds.pollFirst();

        if (id != null) {
            return id;
        }

        return genomeIdFactory.next();
    }

    public Genome createGenesisGenome(final Context context) {
        Genome genome = new Genome(createGenomeId());

        context.nodeGenes().setupInitial(genome);
        context.connectionGenes().setupInitial(genome);

        return genome;
    }

    public void disposeId(final Genome genome) {
        disposedGenomeIds.add(genome.getId());
    }

    public int getDisposedGenomeIdCount() {
        return disposedGenomeIds.size();
    }

    public void clearGenomeIds() {
        disposedGenomeIds.clear();
        genomeIdFactory.reset();
    }
}
