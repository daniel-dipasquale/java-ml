package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ConnectionGene<T extends Comparable<T>> {
    private final InnovationId<T> innovationId;
    @Setter
    private float weight;
    private int cyclesAllowed;
    private boolean expressed;

    ConnectionGene(final InnovationId<T> innovationId, final float weight) {
        this.innovationId = innovationId;
        this.weight = weight;
        this.cyclesAllowed = 1;
        this.expressed = true;
    }

    void increaseCyclesAllowed() {
        cyclesAllowed++;
    }

    void disable() {
        expressed = false;
    }

    boolean toggleExpressed() {
        return expressed = !expressed;
    }

    public ConnectionGene<T> createCopy(final boolean expressed) {
        return new ConnectionGene<>(innovationId, weight, cyclesAllowed, expressed); // TODO: review the cycles allowed replication
    }
}
