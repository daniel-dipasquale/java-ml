package com.dipasquale.metric;

public interface MetricDatum {
    float getLastValue();

    float getSum();

    int getCount();

    float getAverage();

    float getMinimum();

    float getMaximum();

    float getPercentile(float percentage);

    void add(float value);

    void clear();
}
