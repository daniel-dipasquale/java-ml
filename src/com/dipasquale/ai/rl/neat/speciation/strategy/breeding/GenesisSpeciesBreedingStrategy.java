package com.dipasquale.ai.rl.neat.speciation.strategy.breeding;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.MutationOrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor
public final class GenesisSpeciesBreedingStrategy implements SpeciesBreedingStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 5863616191422907831L;
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;

    @Override
    public void process(final SpeciesBreedingContext breedContext, final List<Species> speciesList) {
        Context.RandomSupport random = context.random();

        for (Species species : speciesList) {
            species.restart(random).stream()
                    .filter(o -> !organismsWithoutSpecies.contains(o))
                    .forEach(Organism::kill);

            if (organismsWithoutSpecies.remove(species.getRepresentative())) { // TODO: figure out a better way of doing this
                organismsToBirth.add(new MutationOrganismFactory(species.getRepresentative()));
            }
        }
    }
}
