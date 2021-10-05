package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;

interface NeuronValueGroup {
    float getValue(SequentialId id);

    float getValue(SequentialId id, SequentialId sourceId);

    void setValue(SequentialId id, float value);

    void addToValue(SequentialId id, float value, SequentialId sourceId);

    void clear();
}
