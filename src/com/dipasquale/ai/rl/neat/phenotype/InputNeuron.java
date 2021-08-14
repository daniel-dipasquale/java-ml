package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class InputNeuron {
    private final SequentialId neuronId;
    private final int recurrentCyclesAllowed;

    @Override
    public String toString() {
        return String.format("%s (%d)", neuronId, recurrentCyclesAllowed);
    }
}
