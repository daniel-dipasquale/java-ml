package com.dipasquale.common.random;

import java.io.Serial;
import java.io.Serializable;

final class BoundedRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 7802926567112564485L;
    private final RandomSupport randomSupport;
    private final float minimumFloat;
    private final float maximumFloat;
    private final double minimumDouble;
    private final double maximumDouble;

    BoundedRandomSupport(final RandomSupport randomSupport, final double minimum, final double maximum) {
        this.randomSupport = randomSupport;
        this.minimumFloat = (float) minimum;
        this.maximumFloat = (float) maximum;
        this.minimumDouble = minimum;
        this.maximumDouble = maximum;
    }

    @Override
    public float nextFloat() {
        return randomSupport.nextFloat(minimumFloat, maximumFloat);
    }

    @Override
    public double nextDouble() {
        return randomSupport.nextDouble(minimumDouble, maximumDouble);
    }
}
