package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.internal.Id;
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

    public static ConnectionType getType(final Id inputNodeId, final Id outputNodeId) {
        int comparison = inputNodeId.compareTo(outputNodeId);

        if (comparison == 0) {
            return ConnectionType.REFLEXIVE;
        }

        if (comparison > 0) {
            return ConnectionType.BACKWARD;
        }

        return ConnectionType.FORWARD;
    }

    @Override
    public String toString() {
        return String.format("innovationId: %s, weight: %f, cyclesAllowed: %d", innovationId, weight, cyclesAllowed);
    }
}
