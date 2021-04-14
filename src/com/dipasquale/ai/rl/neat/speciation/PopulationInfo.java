package com.dipasquale.ai.rl.neat.speciation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class PopulationInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -5060171919286103840L;
    private int generation = 1;
    private final PopulationHistoricalMarkings historicalMarkings = new PopulationHistoricalMarkings();

    public void increaseGeneration() {
        generation++;
    }

    public void restartGeneration() {
        generation = 1;
    }
}
