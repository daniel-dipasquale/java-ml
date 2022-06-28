package com.dipasquale.common.random;

import com.dipasquale.common.ArgumentValidatorSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeterministicRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 199669929765894032L;
    private static final float MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private static final double MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private final FloatVenue floatVenue;
    private final DoubleVenue doubleVenue;

    @Override
    public float nextFloat() {
        float value = (float) floatVenue.index / floatVenue.size;

        floatVenue.index = (floatVenue.index + 1) % floatVenue.maximum;

        return value * MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE;
    }

    @Override
    public double nextDouble() {
        double value = (double) doubleVenue.index / doubleVenue.size;

        doubleVenue.index = (doubleVenue.index + 1L) % doubleVenue.maximum;

        return value * MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE;
    }

    private static int getMaximum(final long size) {
        if (size <= (long) Integer.MAX_VALUE) {
            return (int) size;
        }

        return Integer.MAX_VALUE;
    }

    public static DeterministicRandomSupport create(final long size) {
        ArgumentValidatorSupport.ensureGreaterThan(size, 1L, "size");

        FloatVenue floatVenue = new FloatVenue(getMaximum(size));
        DoubleVenue doubleVenue = new DoubleVenue(size);

        return new DeterministicRandomSupport(floatVenue, doubleVenue);
    }

    private static final class FloatVenue {
        private int index;
        private final float size;
        private final int maximum;

        private FloatVenue(final int maximum) {
            this.index = 0;
            this.size = (float) (maximum - 1);
            this.maximum = maximum;
        }
    }

    private static final class DoubleVenue {
        private long index;
        private final double size;
        private final long maximum;

        private DoubleVenue(final long maximum) {
            this.index = 0L;
            this.size = (double) (maximum - 1L);
            this.maximum = maximum;
        }
    }
}
