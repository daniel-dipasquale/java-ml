package com.dipasquale.common.random.float2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class GaussianDistributionSupportTest {
    private static final double MEAN = 0D;
    private static final double STANDARD_DEVIATION = 1D;

    @Test
    public void TEST_1() {
        GaussianDistributionSupport test = new GaussianDistributionSupport(0L);

        Assertions.assertEquals(-1.1250595132795838D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(0.530935451766831D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(-1.501947975750102D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(-2.886574713151337D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(-1.3352556488938523D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(1.2781259377055108D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(1.4646341778193093D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(-0.05497587110404488D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(-0.7172718898064643D, test.nextRandom(MEAN, STANDARD_DEVIATION));
        Assertions.assertEquals(-0.07014005959567751D, test.nextRandom(MEAN, STANDARD_DEVIATION));
    }
}
