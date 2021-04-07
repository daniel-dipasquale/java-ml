package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IntegerFactoryCyclic implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = -5942992984389500841L;
    private final List<? extends IntegerFactory> factories;
    private int index = 0;

    @Override
    public int create() {
        int indexOld = index;

        index = (index + 1) % factories.size();

        return factories.get(indexOld).create();
    }
}
