package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.speciation.core.ReproductionType;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.factory.ObjectAccessor;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextSpeciationSupport implements Context.SpeciationSupport {
    private DefaultContextSpeciationParameters params;
    private GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private ObjectSwitcher<ObjectAccessor<ReproductionType>> randomReproductionTypeGenerator;

    @Override
    public Context.SpeciationParameters params() {
        return params;
    }

    @Override
    public double calculateCompatibility(final DefaultGenome genome1, final DefaultGenome genome2) {
        double compatibility = genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);

        if (Double.isFinite(compatibility)) {
            return compatibility;
        }

        return Double.MAX_VALUE;
    }

    @Override
    public ReproductionType nextReproductionType(final int organisms) {
        return randomReproductionTypeGenerator.getObject().get(organisms);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("speciation.params", params);
        state.put("speciation.genomeCompatibilityCalculator", genomeCompatibilityCalculator);
        state.put("speciation.randomReproductionTypeGenerator", randomReproductionTypeGenerator);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        params = state.get("speciation.params");
        genomeCompatibilityCalculator = state.get("speciation.genomeCompatibilityCalculator");
        randomReproductionTypeGenerator = ObjectSwitcher.switchObject(state.get("speciation.randomReproductionTypeGenerator"), eventLoop != null);
    }
}
