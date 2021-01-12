package com.experimental.metrics;

public interface MetricDatum {
    double getSum();

    long getCount();

    double getAverage();

    double getMinimum();

    double getMaximum();

    double getPth(int major, int minor);
}
