package com.dipasquale.common.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class QuadrupleSigmoidRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final float MINIMUM = 0f;
    private static final float MAXIMUM = 1f;
    private static final UniformRandomSupport UNIFORM_RANDOM_SUPPORT = new UniformRandomSupport();

    private static void assertCommonTraits(final QuadrupleSigmoidRandomSupport randomSupport) {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(randomSupport, ITERATIONS, MINIMUM, MAXIMUM));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(randomSupport, ITERATIONS, 0, 10, List.of(0.05f)));
    }

    @Test
    public void GIVEN_a_quadruple_sigmoid_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        QuadrupleSigmoidRandomSupport test = new QuadrupleSigmoidRandomSupport(UNIFORM_RANDOM_SUPPORT, 0.99D, 0.05D, Math.pow(10D, 3D));

        assertCommonTraits(test);
    }

    @Test
    public void GIVEN_a_deterministic_quadruple_sigmoid_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_predictable_1() {
        QuadrupleSigmoidRandomSupport test = new QuadrupleSigmoidRandomSupport(DeterministicRandomSupport.create(15L), 0.5D, 0.5D, Math.pow(10D, 1.5D));

        Assertions.assertEquals(0f, test.nextFloat());
        Assertions.assertEquals(0.017839601f, test.nextFloat());
        Assertions.assertEquals(0.045922607f, test.nextFloat());
        Assertions.assertEquals(0.14308023f, test.nextFloat());
        Assertions.assertEquals(0.35343274f, test.nextFloat());
        Assertions.assertEquals(0.4511576f, test.nextFloat());
        Assertions.assertEquals(0.4803759f, test.nextFloat());
        Assertions.assertEquals(0.49999997f, test.nextFloat());
        Assertions.assertEquals(0.519624f, test.nextFloat());
        Assertions.assertEquals(0.54884225f, test.nextFloat());
        Assertions.assertEquals(0.646567f, test.nextFloat());
        Assertions.assertEquals(0.8569195f, test.nextFloat());
        Assertions.assertEquals(0.95407724f, test.nextFloat());
        Assertions.assertEquals(0.9821604f, test.nextFloat());
        Assertions.assertEquals(0.99999994f, test.nextFloat());
    }

    @Test
    public void GIVEN_a_deterministic_quadruple_sigmoid_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_predictable_2() {
        QuadrupleSigmoidRandomSupport test = new QuadrupleSigmoidRandomSupport(DeterministicRandomSupport.create(15L), 0.75D, 0.25D, Math.pow(10D, 2D));

        Assertions.assertEquals(0f, test.nextFloat());
        Assertions.assertEquals(0.010860985f, test.nextFloat());
        Assertions.assertEquals(0.3876141f, test.nextFloat());
        Assertions.assertEquals(0.4591618f, test.nextFloat());
        Assertions.assertEquals(0.46916115f, test.nextFloat());
        Assertions.assertEquals(0.47932145f, test.nextFloat());
        Assertions.assertEquals(0.48962516f, test.nextFloat());
        Assertions.assertEquals(0.49999997f, test.nextFloat());
        Assertions.assertEquals(0.5103748f, test.nextFloat());
        Assertions.assertEquals(0.52067846f, test.nextFloat());
        Assertions.assertEquals(0.5308388f, test.nextFloat());
        Assertions.assertEquals(0.5408381f, test.nextFloat());
        Assertions.assertEquals(0.61238545f, test.nextFloat());
        Assertions.assertEquals(0.98913896f, test.nextFloat());
        Assertions.assertEquals(0.99999994f, test.nextFloat());
    }

    @Test
    public void GIVEN_a_deterministic_quadruple_sigmoid_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_predictable_3() {
        QuadrupleSigmoidRandomSupport test = new QuadrupleSigmoidRandomSupport(DeterministicRandomSupport.create(15L), 0.91D, 0.1D, Math.pow(10D, 1.75D));

        Assertions.assertEquals(0f, test.nextFloat());
        Assertions.assertEquals(0.3859449f, test.nextFloat());
        Assertions.assertEquals(0.48211545f, test.nextFloat());
        Assertions.assertEquals(0.48724204f, test.nextFloat());
        Assertions.assertEquals(0.49040732f, test.nextFloat());
        Assertions.assertEquals(0.49358058f, test.nextFloat());
        Assertions.assertEquals(0.49678287f, test.nextFloat());
        Assertions.assertEquals(0.49999997f, test.nextFloat());
        Assertions.assertEquals(0.50321704f, test.nextFloat());
        Assertions.assertEquals(0.5064193f, test.nextFloat());
        Assertions.assertEquals(0.5095926f, test.nextFloat());
        Assertions.assertEquals(0.51275784f, test.nextFloat());
        Assertions.assertEquals(0.51788443f, test.nextFloat());
        Assertions.assertEquals(0.61405456f, test.nextFloat());
        Assertions.assertEquals(0.9999998f, test.nextFloat());
    }

    @Test
    public void GIVEN_a_deterministic_quadruple_sigmoid_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_predictable_4() {
        QuadrupleSigmoidRandomSupport test = new QuadrupleSigmoidRandomSupport(DeterministicRandomSupport.create(15L), 0.99D, 0.05D, Math.pow(10D, 3D));

        Assertions.assertEquals(0f, test.nextFloat());
        Assertions.assertEquals(0.49043015f, test.nextFloat());
        Assertions.assertEquals(0.49353895f, test.nextFloat());
        Assertions.assertEquals(0.49591345f, test.nextFloat());
        Assertions.assertEquals(0.4975653f, test.nextFloat());
        Assertions.assertEquals(0.49866414f, test.nextFloat());
        Assertions.assertEquals(0.4994141f, test.nextFloat());
        Assertions.assertEquals(0.49999997f, test.nextFloat());
        Assertions.assertEquals(0.50058585f, test.nextFloat());
        Assertions.assertEquals(0.5013358f, test.nextFloat());
        Assertions.assertEquals(0.50243455f, test.nextFloat());
        Assertions.assertEquals(0.50408643f, test.nextFloat());
        Assertions.assertEquals(0.506461f, test.nextFloat());
        Assertions.assertEquals(0.50956976f, test.nextFloat());
        Assertions.assertEquals(0.99999976f, test.nextFloat());
    }
}
