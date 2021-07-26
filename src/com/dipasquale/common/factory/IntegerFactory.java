package com.dipasquale.common.factory;

import java.util.List;

@FunctionalInterface
public interface IntegerFactory {
    int create();

    static IntegerFactory createLiteral(final int value) {
        return () -> value;
    }

    static IntegerFactory createIllegalState(final String message) {
        return () -> {
            throw new IllegalStateException(message);
        };
    }

    static IntegerFactory createCyclic(final List<? extends IntegerFactory> factories) {
        int[] index = new int[1];

        return () -> {
            int indexOld = index[0];

            index[0] = (index[0] + 1) % factories.size();

            return factories.get(indexOld).create();
        };
    }
}
