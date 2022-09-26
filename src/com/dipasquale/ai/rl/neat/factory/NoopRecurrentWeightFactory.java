package com.dipasquale.ai.rl.neat.factory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopRecurrentWeightFactory implements RecurrentWeightFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -8312992005103689278L;
    private static final List<Float> EMPTY = List.of();
    private static final NoopRecurrentWeightFactory INSTANCE = new NoopRecurrentWeightFactory();

    public static NoopRecurrentWeightFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public List<Float> create() {
        return EMPTY;
    }

    @Override
    public List<Float> clone(final List<Float> recurrentWeights) {
        return EMPTY;
    }

    @Override
    public List<Float> createAverage(final List<Float> recurrentWeights1, final List<Float> recurrentWeights2) {
        return EMPTY;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
