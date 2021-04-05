package com.dipasquale.ai.common;

import java.io.Serializable;

public interface MetricDatum extends Serializable {
    float getLastValue();

    float getSum();

    int getCount();

    float getAverage();

    float getMinimum();

    float getMaximum();

    float getPth(float percentage);

    void add(float value);

    void clear();
}
