package com.dipasquale.ai.rl.neat.phenotype;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdentityNeuronLayerNormalizer implements NeuronLayerNormalizer, Serializable {
    @Serial
    private static final long serialVersionUID = 2057105751591991863L;
    private static final IdentityNeuronLayerNormalizer INSTANCE = new IdentityNeuronLayerNormalizer();

    public static IdentityNeuronLayerNormalizer getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public float[] getValues(final NeuronLayerReader reader) {
        float[] values = new float[reader.size()];

        for (int i = 0; i < values.length; i++) {
            values[i] = reader.getValue(i);
        }

        return values;
    }
}
