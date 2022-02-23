package com.dipasquale.common.random.float2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DirichletDistributionSupportTest {
    @Test
    public void TEST_1() {
        double[] shapes = {0.5D, 0.5D, 0.5D, 0.5D, 0.5D};
        DeterministicRandomSupport deterministicRandomSupport = new DeterministicRandomSupport(10L);
        GaussianDistributionSupport gaussianDistributionSupport = new GaussianDistributionSupport(0L);
        GammaDistributionSupport gammaDistributionSupport = new GammaDistributionSupport(deterministicRandomSupport, gaussianDistributionSupport);
        DirichletDistributionSupport test = new DirichletDistributionSupport(gammaDistributionSupport);

        Assertions.assertArrayEquals(new double[]{0D, 0.16666666666666669D, 0.6666666666666667D, 0D, 0.16666666666666669D}, test.nextRandom(shapes));
    }
}
