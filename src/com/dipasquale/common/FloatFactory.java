package com.dipasquale.common;

import java.io.Serializable;
import java.util.List;

@FunctionalInterface
public interface FloatFactory extends Serializable {
    float create();

    static FloatFactory createLiteral(final float value) {
        return () -> value;
    }

    static FloatFactory createIllegalState(final String message) {
        return () -> {
            throw new IllegalStateException(message);
        };
    }

    static FloatFactory createCyclic(final List<? extends FloatFactory> factories) {
        int[] index = new int[1];

        return () -> {
            int indexOld = index[0];

            index[0] = (index[0] + 1) % factories.size();

            return factories.get(indexOld).create();
        };
    }
}
