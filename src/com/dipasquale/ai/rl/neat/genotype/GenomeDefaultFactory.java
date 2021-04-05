package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.context.Context;

import java.io.Serializable;

@FunctionalInterface
public interface GenomeDefaultFactory extends Serializable {
    GenomeDefault create(Context context);
}
