package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;

interface NeuronValueGroup {
    float getValue(SequentialId id);

    void setValue(SequentialId id, float value);

    void addToValue(SequentialId id, float delta, SequentialId sourceId);

    void clear();
}
