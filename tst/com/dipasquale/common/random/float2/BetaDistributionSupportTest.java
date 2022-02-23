package com.dipasquale.common.random.float2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class BetaDistributionSupportTest {
    @Test
    public void TEST_1() {
        double alpha = 1D;
        double beta = 1D;
        DeterministicRandomSupport deterministicRandomSupport = new DeterministicRandomSupport(10L);
        BetaDistributionSupport test = new BetaDistributionSupport(deterministicRandomSupport);

        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.22222222222222224D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.4444444444444443D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.6666666666666665D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.22222222222222224D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.4444444444444443D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.6666666666666665D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.22222222222222224D, test.nextRandom(alpha, beta));
    }

    @Test
    public void TEST_2() {
        double alpha = 2D;
        double beta = 2D;
        DeterministicRandomSupport deterministicRandomSupport = new DeterministicRandomSupport(10L);
        BetaDistributionSupport test = new BetaDistributionSupport(deterministicRandomSupport);

        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.2919696316541213D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.4606350574007561D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.6201368378738459D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.2919696316541213D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.4606350574007561D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.6201368378738459D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.2919696316541213D, test.nextRandom(alpha, beta));
    }

    @Test
    public void TEST_3() {
        double alpha = 0.5D;
        double beta = 0.5D;
        DeterministicRandomSupport deterministicRandomSupport = new DeterministicRandomSupport(10L);
        BetaDistributionSupport test = new BetaDistributionSupport(deterministicRandomSupport);

        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.07547169811320753D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.3902439024390242D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.7999999999999997D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.07547169811320753D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.3902439024390242D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.7999999999999997D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0D, test.nextRandom(alpha, beta));
        Assertions.assertEquals(0.07547169811320753D, test.nextRandom(alpha, beta));
    }
}
