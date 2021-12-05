package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.common.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DefaultRecurrentNeuronStateGroup extends RecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "DEFAULT_H";
    private final NeuronMemory memory;

    @Override
    protected float getRecurrentValue(final Id id, final Id inputId) {
        return getValue(id) + getValue(memory, HIDDEN_DIMENSION, id);
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(HIDDEN_DIMENSION, id, value, inputId);
    }
}
