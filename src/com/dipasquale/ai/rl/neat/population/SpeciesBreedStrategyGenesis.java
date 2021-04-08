package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactory;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactoryMutation;
import com.dipasquale.ai.rl.neat.genotype.Species;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyGenesis implements SpeciesBreedStrategy {
    @Serial
    private static final long serialVersionUID = 4583019667584298822L;
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;

    @Override
    public void process(final SpeciesBreedContext breedContext, final List<Species> speciesList) {
        Context.Random random = context.random();
        Context.GeneralSupport general = context.general();

        for (Species species : speciesList) {
            species.restart(random).stream()
                    .filter(o -> !organismsWithoutSpecies.contains(o))
                    .forEach(o -> o.kill(general));

            if (organismsWithoutSpecies.remove(species.getRepresentative())) { // TODO: figure out a better way of doing this
                organismsToBirth.add(new OrganismFactoryMutation(species.getRepresentative()));
            }
        }
    }
}
