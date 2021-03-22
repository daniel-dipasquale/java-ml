package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.SequentialId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class NeuronOutput {
    private final SequentialId neuronId;
    private final float connectionWeight;

    @Override
    public String toString() {
        return String.format("%s = %f", neuronId, connectionWeight);
    }
}
