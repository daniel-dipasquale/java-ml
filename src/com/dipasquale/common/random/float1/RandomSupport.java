package com.dipasquale.common.random.float1;

@FunctionalInterface
public interface RandomSupport {
    float next();

    default float next(final float min, final float max) {
        float value = next();

        return value * (max - min) + min;
    }

    default int next(final int min, final int max) {
        int maxStep1 = max - 1;
        float value = next();
        float minFixed = (float) min;
        float maxFixed = (float) maxStep1;

        return (int) (value * maxFixed - value * minFixed + value + minFixed);
    }

    default RandomSupport bounded(final float min, final float max) {
        return new BoundedRandomSupport(this, min, max);
    }

    default boolean isBetween(final float min, final float max) {
        float value = next();

        return Float.compare(value, min) >= 0 && Float.compare(value, max) < 0;
    }

    default boolean isLessThan(final float max) {
        return isBetween(0f, max);
    }
}
