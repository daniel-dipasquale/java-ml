package com.dipasquale.ai.rl.neat;

@FunctionalInterface
public interface NeatCollectiveClient {
    float[] activate(float[] inputs);
}
