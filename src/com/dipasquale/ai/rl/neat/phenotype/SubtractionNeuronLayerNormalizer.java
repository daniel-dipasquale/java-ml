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
            float value1 = reader.getValue(index);
            float value2 = reader.getValue(index + 1);

            values[i] = switch (reader.getType(index)) {
                case RE_LU, SIGMOID, STEEPENED_SIGMOID, STEP -> Math.abs(value1 - value2);

                case TAN_H -> Math.abs(value1 - value2) - 1f;

                case IDENTITY -> Float.compare(value1, value2) >= 0
                        ? value1 - value2
                        : value2 - value1;
            };
        }

        return values;
    }
}
