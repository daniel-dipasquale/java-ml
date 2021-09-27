package com.dipasquale.ai.rl.neat.speciation.core;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
public final class PopulationState implements Serializable {
    @Serial
    private static final long serialVersionUID = -5060171919286103840L;
    private int iteration = 1;
    private int generation = 1;

    public void increaseGeneration() {
        generation++;
    }

    public void restart() {
        iteration++;
        generation = 1;
    }
}
