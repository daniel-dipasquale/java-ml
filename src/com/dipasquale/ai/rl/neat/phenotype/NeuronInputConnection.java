package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class NeuronInputConnection implements Serializable {
    @Serial
    private static final long serialVersionUID = 2600008683072648717L;
    private final Id sourceNeuronId;
    private final int cyclesAllowed;

    @Override
    public String toString() {
        return String.format("%s (cyclesAllowed: %d)", sourceNeuronId, cyclesAllowed);
    }
}
