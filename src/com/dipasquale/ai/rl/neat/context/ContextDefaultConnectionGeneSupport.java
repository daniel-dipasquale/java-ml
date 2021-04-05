package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.common.FloatFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class ContextDefaultConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private final boolean multipleRecurrentCyclesAllowed;
    private final SequentialIdFactory innovationIdFactory;
    private final Map<DirectedEdge, InnovationId> innovationIds;
    private final FloatFactory weightFactory;
    private final WeightPerturber weightPerturber;

    @Override
    public boolean multipleRecurrentCyclesAllowed() {
        return multipleRecurrentCyclesAllowed;
    }

    @Override
    public InnovationId getOrCreateInnovationId(final DirectedEdge directedEdge) {
        return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId(de, innovationIdFactory.next()));
    }

    @Override
    public float nextWeight() {
        return weightFactory.create();
    }

    @Override
    public float perturbWeight(final float weight) {
        return weightPerturber.next(weight);
    }

    @Override
    public void reset() {
        innovationIdFactory.reset();
        innovationIds.clear();
    }
}
