package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IntegerFactoryCyclicCas implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = -3472996940536839698L;
    private final List<? extends IntegerFactory> factories;
    private final AtomicInteger index = new AtomicInteger();

    @Override
    public int create() {
        int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % factories.size());

        return factories.get(indexFixed).create();
    }
}
