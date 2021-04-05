package com.dipasquale.ai.common;

import java.io.Serializable;

public interface FitnessDeterminer extends Serializable {
    float get();

    void add(float fitness);

    void clear();
}
