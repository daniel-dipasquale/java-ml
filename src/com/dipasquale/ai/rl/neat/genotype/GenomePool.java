package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.IntegerIdFactory;
import com.dipasquale.ai.rl.neat.NeatContext;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.PriorityQueue;
import java.util.Queue;

@RequiredArgsConstructor
public final class GenomePool implements Serializable {
    @Serial
    private static final long serialVersionUID = 6680979157037947466L;
    private final IntegerIdFactory genomeIdFactory = new IntegerIdFactory();
    private final Queue<Integer> disposedGenomeIds = new PriorityQueue<>(Integer::compare); // TODO: ensure this algorithm works with a dynamic population size

    public int createGenomeId() {
        Integer id = disposedGenomeIds.poll();

        if (id != null) {
            return id;
        }

        return genomeIdFactory.next();
    }

    public Genome createGenesisGenome(final NeatContext context) {
        Genome genome = new Genome(createGenomeId());

        context.getNodeGenes().setupInitial(genome);
        context.getConnectionGenes().setupInitial(genome);

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
