package com.dipasquale.ai.rl.neat.phenotype;

@FunctionalInterface
public interface NeuronLayerNormalizer {
    float[] getValues(NeuronLayerReader reader);
}
