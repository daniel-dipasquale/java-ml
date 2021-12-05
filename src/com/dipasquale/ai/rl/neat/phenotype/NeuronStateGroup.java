package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.common.Id;

interface NeuronStateGroup {
    float getValue(Id id);

    float getValue(Id id, Id inputId);

    void setValue(Id id, float value);

    void addValue(Id id, float value, Id inputId);

    void clear();
}
