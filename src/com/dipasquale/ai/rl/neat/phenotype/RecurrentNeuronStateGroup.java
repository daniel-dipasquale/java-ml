package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RecurrentNeuronStateGroup extends AbstractRecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "VANILLA_H";
    private final NeuronMemory memory;

    @Override
    protected float getRecurrentValue(final Id id) {
        return getValue(id) + getValue(memory, HIDDEN_DIMENSION, id);
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(HIDDEN_DIMENSION, id, value, inputId);
    }
}
