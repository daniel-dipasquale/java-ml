package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.common.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ShortTermNeuronStateGroup extends RecurrentNeuronStateGroup {
    private static final String SHORT_TERM_DIMENSION = "HS";
    private final NeuronMemory memory;

    @Override
    protected float getRecurrentValue(final Id id, final Id inputId) {
        return getValue(id) + getValue(memory, SHORT_TERM_DIMENSION, id);
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(SHORT_TERM_DIMENSION, id, value, inputId);
    }
}
