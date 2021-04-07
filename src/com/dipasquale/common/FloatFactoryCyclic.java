package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FloatFactoryCyclic implements FloatFactory {
    @Serial
    private static final long serialVersionUID = 6823689708687943747L;
    private final List<? extends FloatFactory> factories;
    private int index = 0;

    @Override
    public float create() {
        int indexOld = index;

        index = (index + 1) % factories.size();

        return factories.get(indexOld).create();
    }
}
