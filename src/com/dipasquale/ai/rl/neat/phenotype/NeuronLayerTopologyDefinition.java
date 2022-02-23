package com.dipasquale.ai.rl.neat.phenotype;

@FunctionalInterface
public interface NeuronLayerTopologyDefinition {
    float[] getValues(NeuronLayerReader reader);
}
