package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class OutputNeuron implements Serializable {
    @Serial
    private static final long serialVersionUID = -449674642630253269L;
    private final SequentialId neuronId;
    private final float connectionWeight;

    @Override
    public String toString() {
        return String.format("%s = %f", neuronId, connectionWeight);
    }
}
