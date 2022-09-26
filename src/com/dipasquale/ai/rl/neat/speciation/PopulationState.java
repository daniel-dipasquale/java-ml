package com.dipasquale.ai.rl.neat.speciation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public final class PopulationState implements Serializable {
    @Serial
    private static final long serialVersionUID = -5060171919286103840L;
    private int iteration = 1;
    private int generation = 1;

    public void advanceGeneration() {
        generation++;
    }

    public void restart() {
        iteration++;
        generation = 1;
    }

    public PopulationState createClone() {
        PopulationState state = new PopulationState();

        state.iteration = iteration;
        state.generation = generation;

        return state;
    }
}
