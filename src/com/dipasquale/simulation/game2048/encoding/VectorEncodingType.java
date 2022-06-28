package com.dipasquale.simulation.game2048.encoding;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum VectorEncodingType {
    INTEGER,
    FLOAT;

    public float encode(final int value) {
        return switch (this) {
            case INTEGER -> (float) value;

            case FLOAT -> Float.intBitsToFloat(value);
        };
    }

    public float[] encode(final int[] values) {
        float[] encoded = new float[values.length];

        for (int i = 0; i < values.length; i++) {
            encoded[i] = encode(values[i]);
        }

        return encoded;
    }
}
