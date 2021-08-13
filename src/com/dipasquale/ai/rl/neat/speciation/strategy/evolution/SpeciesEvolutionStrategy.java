/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.speciation.strategy.evolution;

import com.dipasquale.ai.rl.neat.speciation.core.Species;

public interface SpeciesEvolutionStrategy {
    void process(SpeciesEvolutionContext evolutionContext, Species species, boolean speciesSurvives);

    void postProcess(SpeciesEvolutionContext evolutionContext);
}
