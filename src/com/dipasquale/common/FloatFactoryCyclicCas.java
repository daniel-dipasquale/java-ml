package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FloatFactoryCyclicCas implements FloatFactory {
    @Serial
    private static final long serialVersionUID = 5430185244304459717L;
    private final List<? extends FloatFactory> factories;
    private final AtomicInteger index = new AtomicInteger();

    @Override
    public float create() {
        int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % factories.size());

        return factories.get(indexFixed).create();
    }
}
