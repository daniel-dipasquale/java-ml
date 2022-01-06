package com.dipasquale.ai.rl.neat.phenotype;

import java.io.Serial;
import java.io.Serializable;

public final class SubtractionNeuronLayerNormalizer implements NeuronLayerNormalizer, Serializable {
    @Serial
    private static final long serialVersionUID = -7847929372961392239L;

    @Override
    public float[] getValues(final NeuronLayerReader reader) {
        float[] values = new float[reader.size() / 2];

        for (int i = 0; i < values.length; i++) {
            int index = i * 2;
            float value = reader.getValue(index) - reader.getValue(index + 1);

            values[i] = switch (reader.getType(index)) {
                case RE_LU, SIGMOID, STEEPENED_SIGMOID, STEP -> Math.abs(value);

                case TAN_H -> value / 2f;

                case IDENTITY -> value;

                case RANDOM -> throw new IllegalStateException("exception will not occur, just avoiding a compilation error");
            };
        }

        return values;
    }
}
