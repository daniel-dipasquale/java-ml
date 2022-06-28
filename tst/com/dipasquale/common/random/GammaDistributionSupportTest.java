package com.dipasquale.common.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class GammaDistributionSupportTest {
    @Test
    public void TEST_1() {
        double shape = 1D;
        double scale = 1D;
        DeterministicRandomSupport deterministicRandomSupport = DeterministicRandomSupport.create(10L);
        GaussianDistributionSupport gaussianDistributionSupport = new GaussianDistributionSupport(0L);
        GammaDistributionSupport test = new GammaDistributionSupport(deterministicRandomSupport, gaussianDistributionSupport);

        Assertions.assertEquals(0.1053826510473247D, test.nextRandom(shape, scale));
        Assertions.assertEquals(1.2009268183025863D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.03859019319727584D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.06274963364970045D, test.nextRandom(shape, scale));
        Assertions.assertEquals(2.3494989498292513D, test.nextRandom(shape, scale));
        Assertions.assertEquals(2.7201044384101514D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.6227789676829603D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.2357704629749819D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.6110217714319618D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.7777433354975043D, test.nextRandom(shape, scale));
    }

    @Test
    public void TEST_2() {
        double shape = 2D;
        double scale = 2D;
        DeterministicRandomSupport deterministicRandomSupport = DeterministicRandomSupport.create(10L);
        GaussianDistributionSupport gaussianDistributionSupport = new GaussianDistributionSupport(0L);
        GammaDistributionSupport test = new GammaDistributionSupport(deterministicRandomSupport, gaussianDistributionSupport);

        Assertions.assertEquals(1.1905727249021174D, test.nextRandom(shape, scale));
        Assertions.assertEquals(4.900718583274958D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.7648142213441387D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.05506966699947653D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.9377280686410561D, test.nextRandom(shape, scale));
        Assertions.assertEquals(7.842312573061942D, test.nextRandom(shape, scale));
        Assertions.assertEquals(8.72537714784828D, test.nextRandom(shape, scale));
        Assertions.assertEquals(3.193391608487994D, test.nextRandom(shape, scale));
        Assertions.assertEquals(1.8031577222957684D, test.nextRandom(shape, scale));
        Assertions.assertEquals(6.241487813284579D, test.nextRandom(shape, scale));
    }

    @Test
    public void TEST_3() {
        double shape = 0.5D;
        double scale = 0.5D;
        DeterministicRandomSupport deterministicRandomSupport = DeterministicRandomSupport.create(10L);
        GaussianDistributionSupport gaussianDistributionSupport = new GaussianDistributionSupport(0L);
        GammaDistributionSupport test = new GammaDistributionSupport(deterministicRandomSupport, gaussianDistributionSupport);

        Assertions.assertEquals(0D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.03461020399952087D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.13844081599808347D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.03461020399952087D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.13844081599808347D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.03461020399952087D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0.13844081599808347D, test.nextRandom(shape, scale));
        Assertions.assertEquals(0D, test.nextRandom(shape, scale));
    }
}
