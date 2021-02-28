package com.dipasquale.ai.common;

public interface MetricDatum {
    float getSum();

    int getCount();

    float getAverage();

    float getMinimum();

    float getMaximum();

    float getPth(float percentage);
}
