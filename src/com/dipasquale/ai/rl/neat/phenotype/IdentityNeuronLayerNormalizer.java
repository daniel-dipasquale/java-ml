package com.dipasquale.ai.rl.neat.phenotype;

import java.io.Serial;
import java.io.Serializable;

public final class IdentityNeuronLayerNormalizer implements NeuronLayerNormalizer, Serializable {
    @Serial
    private static final long serialVersionUID = 2057105751591991863L;

    @Override
    public float[] getValues(final NeuronLayerReader reader) {
        float[] values = new float[reader.size()];

        for (int i = 0; i < values.length; i++) {
            values[i] = reader.getValue(i);
        }

        return values;
    }
}
