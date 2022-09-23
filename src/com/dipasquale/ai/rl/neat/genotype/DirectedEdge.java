package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class DirectedEdge implements Serializable {
    @Serial
    private static final long serialVersionUID = 5476428602513687108L;
    private final Id sourceNodeGeneId;
    private final Id targetNodeGeneId;

    public DirectedEdge(final NodeGene sourceNodeGene, final NodeGene targetNodeGene) {
        this.sourceNodeGeneId = sourceNodeGene.getId();
        this.targetNodeGeneId = targetNodeGene.getId();
    }

    @Override
    public String toString() {
        return String.format("%s:%s", sourceNodeGeneId, targetNodeGeneId);
    }
}
