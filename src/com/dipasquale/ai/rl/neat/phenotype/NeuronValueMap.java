package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;

interface NeuronValueMap {
    float getValue(SequentialId id);

    void setValue(SequentialId id, float value);

    void addToValue(SequentialId id, SequentialId fromId, float delta);

    void clear();
}
