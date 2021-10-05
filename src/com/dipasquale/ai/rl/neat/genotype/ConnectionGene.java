package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.AccessLevel;
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
    @Setter(AccessLevel.PACKAGE)
    private float weight;
    private int cyclesAllowed;

    public ConnectionGene(final InnovationId innovationId, final float weight) {
        this.innovationId = innovationId;
        this.weight = weight;
        this.cyclesAllowed = 1;
    }

    void addCyclesAllowed(final int delta) {
        cyclesAllowed = Math.min(cyclesAllowed + delta, 0);
    }

    boolean isExpressed() {
        return cyclesAllowed > 0;
    }

    ConnectionGene createCopy(final int cyclesAllowed) {
        return new ConnectionGene(innovationId, weight, cyclesAllowed);
    }

    ConnectionGene createClone() {
        return createCopy(cyclesAllowed);
    }

    public static boolean isRecurrent(final SequentialId sourceNodeId, final SequentialId targetNodeId) {
        return sourceNodeId.compareTo(targetNodeId) >= 0;
    }

    @Override
    public String toString() {
        return String.format("innovationId: %s, weight: %f, cyclesAllowed: %d", innovationId, weight, cyclesAllowed);
    }
}
