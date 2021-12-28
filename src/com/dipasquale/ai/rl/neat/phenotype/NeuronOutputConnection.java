package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronOutputConnection implements Serializable {
    @Serial
    private static final long serialVersionUID = -449674642630253269L;
    @Getter
    private final Id targetNeuronId;
    @Getter
    private final float weight;
    private final List<Float> recurrentWeights;

    public float getRecurrentWeight(final int index) {
        return recurrentWeights.get(index);
    }

    @Override
    public String toString() {
        return String.format("%f => %s", weight, targetNeuronId);
    }
}
