package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;

import java.io.Serial;

final class SettingsGenomeFactoryNoConnections implements GenomeDefaultFactory {
    @Serial
    private static final long serialVersionUID = 2703446050049038672L;

    @Override
    public GenomeDefault create(final Context context) {
        GenomeDefault genome = new GenomeDefault(context);

        context.nodes().inputNodes().forEach(genome::addNode);
        context.nodes().outputNodes().forEach(genome::addNode);
        context.nodes().biasNodes().forEach(genome::addNode);

        return genome;
    }
}
