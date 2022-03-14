package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LimitSupport {
    public static float getFiniteValue(final float value) {
        if (value == Float.POSITIVE_INFINITY) {
            return Float.MAX_VALUE;
        }

        if (value == Float.NEGATIVE_INFINITY) {
            return -Float.MAX_VALUE;
        }

        return value;
    }

    public static float getPositiveFiniteValue(final float value) {
        if (value == Float.POSITIVE_INFINITY) {
            return Float.MAX_VALUE;
        }

        if (value == Float.NEGATIVE_INFINITY) {
            return 0f;
        }

        return Math.max(value, 0f);
    }

    public static double getFiniteValue(final double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return Double.MAX_VALUE;
        }

        if (value == Double.NEGATIVE_INFINITY) {
            return -Double.MAX_VALUE;
        }

        return value;
    }

    public static double getPositiveFiniteValue(final double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return Double.MAX_VALUE;
        }

        if (value == Double.NEGATIVE_INFINITY) {
            return -Double.MAX_VALUE;
        }

        return value;
    }
}
