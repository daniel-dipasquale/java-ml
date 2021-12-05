package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.common.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class NeuronOutputConnection implements Serializable {
    @Serial
    private static final long serialVersionUID = -449674642630253269L;
    private final Id outputNeuronId;
    private final float connectionWeight;

    @Override
    public String toString() {
        return String.format("%f => %s", connectionWeight, outputNeuronId);
    }
}
