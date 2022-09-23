package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
public final class ConnectionGene implements Serializable {
    @Serial
    private static final long serialVersionUID = -72756908718555853L;
    private final InnovationId innovationId;
    @Setter(AccessLevel.PACKAGE)
    private float weight;
    private List<Float> recurrentWeights;
    private int cyclesAllowed;

    ConnectionGene(final InnovationId innovationId, final float weight, final List<Float> recurrentWeights) {
        this.innovationId = innovationId;
        this.weight = weight;
        this.recurrentWeights = recurrentWeights;
        this.cyclesAllowed = 1;
    }

    void addCyclesAllowed(final int delta) {
        cyclesAllowed = Math.min(cyclesAllowed + delta, 0);
    }

    boolean isExpressed() {
        return cyclesAllowed > 0;
    }

    ConnectionGene createCopy(final Context.ConnectionGeneSupport connectionGeneSupport, final int cyclesAllowed) {
        return new ConnectionGene(innovationId, weight, connectionGeneSupport.cloneRecurrentWeights(recurrentWeights), cyclesAllowed);
    }

    ConnectionGene createClone(final Context.ConnectionGeneSupport connectionGeneSupport) {
        return createCopy(connectionGeneSupport, cyclesAllowed);
    }

    public static ConnectionGeneType getType(final Id sourceNodeId, final Id targetNodeId) {
        int comparison = sourceNodeId.compareTo(targetNodeId);

        if (comparison == 0) {
            return ConnectionGeneType.REFLEXIVE;
        }

        if (comparison > 0) {
            return ConnectionGeneType.BACKWARD;
        }

        return ConnectionGeneType.FORWARD;
    }

    @Override
    public String toString() {
        return String.format("innovationId: %s, weight: %f, cyclesAllowed: %d", innovationId, weight, cyclesAllowed);
    }
}
