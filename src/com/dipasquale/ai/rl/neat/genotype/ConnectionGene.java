package com.dipasquale.ai.rl.neat.genotype;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Getter
public final class ConnectionGene implements Serializable {
    @Serial
    private static final long serialVersionUID = -72756908718555853L;
    private final InnovationId innovationId;
    @Setter(AccessLevel.PACKAGE)
    private float weight;
    private int recurrentCyclesAllowed;
    private boolean expressed;

    public ConnectionGene(final InnovationId innovationId, final float weight) {
        this.innovationId = innovationId;
        this.weight = weight;
        this.recurrentCyclesAllowed = 0;
        this.expressed = true;
    }

    void increaseCyclesAllowed() {
        recurrentCyclesAllowed++;
    }

    void disable() {
        expressed = false;
    }

    boolean toggleExpressed() {
        return expressed = !expressed;
    }

    public ConnectionGene createCopy(final boolean expressed) {
        return new ConnectionGene(innovationId, weight, recurrentCyclesAllowed, expressed);
    }

    public ConnectionGene createClone() {
        return createCopy(expressed);
    }
}
