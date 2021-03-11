package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class ConnectionGene {
    private final InnovationId innovationId;
    @Setter
    private float weight;
    private int recurrentCyclesAllowed;
    private boolean expressed;

    ConnectionGene(final InnovationId innovationId, final float weight) {
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
}
