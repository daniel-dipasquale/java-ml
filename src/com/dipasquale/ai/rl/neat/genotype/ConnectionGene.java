package com.dipasquale.ai.rl.neat.genotype;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public final class ConnectionGene implements Serializable {
    @Serial
    private static final long serialVersionUID = -72756908718555853L;
    private final InnovationId innovationId;
    @Setter
    private float weight;
    private int cyclesAllowed;

    public ConnectionGene(final InnovationId innovationId, final float weight) {
        this.innovationId = innovationId;
        this.weight = weight;
        this.cyclesAllowed = 1;
    }

    public void addCyclesAllowed(final int delta) {
        cyclesAllowed = Math.min(cyclesAllowed + delta, 0);
    }

    public boolean isExpressed() {
        return cyclesAllowed > 0;
    }

    public ConnectionGene createCopy(final int cyclesAllowed) {
        return new ConnectionGene(innovationId, weight, cyclesAllowed);
    }

    public ConnectionGene createClone() {
        return createCopy(cyclesAllowed);
    }
}
