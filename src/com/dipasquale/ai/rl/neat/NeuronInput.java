package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class NeuronInput {
    private final SequentialId neuronId;
    private final int recurrentCyclesAllowed;
}
