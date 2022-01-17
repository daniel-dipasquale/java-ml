package com.dipasquale.ai.rl.neat.phenotype;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TwoSolutionNeuronLayerNormalizer implements NeuronLayerNormalizer, Serializable {
    @Serial
    private static final long serialVersionUID = -7847929372961392239L;
    private static final TwoSolutionNeuronLayerNormalizer INSTANCE = new TwoSolutionNeuronLayerNormalizer();

    public static TwoSolutionNeuronLayerNormalizer getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public float[] getValues(final NeuronLayerReader reader) {
        float[] values = new float[reader.size() / 2];

        for (int i = 0; i < values.length; i++) {
            int index = i * 2;
            float value1 = reader.getValue(index);
            float value2 = reader.getValue(index + 1);

            values[i] = switch (reader.getType(index)) {
                case IDENTITY -> Float.compare(value1, value2) >= 0
                        ? value1 - value2
                        : value2 - value1;

                case RE_LU, SIGMOID, STEEPENED_SIGMOID, STEP -> Math.abs(value1 - value2);

                case TAN_H -> Math.abs(value1 - value2) - 1f;
            };
        }

        return values;
    }
}
