package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class InputNeuron implements Serializable {
    @Serial
    private static final long serialVersionUID = 2600008683072648717L;
    private final SequentialId neuronId;
    private final int recurrentCyclesAllowed;

    @Override
    public String toString() {
        return String.format("%s (%d)", neuronId, recurrentCyclesAllowed);
    }
}
