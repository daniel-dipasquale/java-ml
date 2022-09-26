package com.dipasquale.ai.rl.neat.generational.factory;

import com.dipasquale.ai.rl.neat.speciation.ReproductionType;
import com.dipasquale.common.factory.ObjectIndexProvider;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class GenerationalReproductionTypeProvider implements GenerationalFactory, ObjectIndexProvider<ReproductionType>, Serializable {
    @Serial
    private static final long serialVersionUID = 6788712949153539093L;
    private final GenerationalEveryReproductionTypeFactory everyReproductionTypeFactory;
    private final GenerationalSingleOrganismReproductionTypeFactory singleOrganismReproductionTypeFactory;

    @Override
    public void reinitialize() {
        everyReproductionTypeFactory.reinitialize();
        singleOrganismReproductionTypeFactory.reinitialize();
    }

    @Override
    public ReproductionType provide(final int organisms) {
        if (organisms >= 2) {
            return everyReproductionTypeFactory.create();
        }

        return singleOrganismReproductionTypeFactory.create();
    }
}
