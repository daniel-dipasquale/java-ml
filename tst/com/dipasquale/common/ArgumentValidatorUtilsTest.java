package com.dipasquale.common;

import com.dipasquale.common.test.ThrowableComparer;
import org.junit.Assert;
import org.junit.Test;

public final class ArgumentValidatorUtilsTest {
    @Test
    public void TEST_1() {
        try {
            ArgumentValidatorUtils.ensureNotNull(null, "object");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("object cannot be null")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureNotNull(new Object(), "object");
    }

    @Test
    public void TEST_2() {
        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10D, 11D, "number", "especially number cannot be less than the limit");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10.000000' cannot be less than '11.000000', additional information: especially number cannot be less than the limit")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10D, 10D, "number", "especially number cannot be less than the limit");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10D, 9D, "number", "especially number cannot be less than the limit");

        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10D, 11D, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10.000000' cannot be less than '11.000000'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10D, 10D, "number");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10D, 9D, "number");
    }

    @Test
    public void TEST_3() {
        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10L, 11L, "number", "especially number cannot be less than the limit");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11', additional information: especially number cannot be less than the limit")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10L, 10L, "number", "especially number cannot be less than the limit");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10L, 9L, "number", "especially number cannot be less than the limit");

        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10L, 11L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10L, 10L, "number");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10L, 9L, "number");
    }

    @Test
    public void TEST_4() {
        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10, 11, "number", "especially number cannot be less than the limit");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11', additional information: especially number cannot be less than the limit")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10, 10, "number", "especially number cannot be less than the limit");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10, 9, "number", "especially number cannot be less than the limit");

        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10, 11, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10, 10, "number");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(10, 9, "number");
    }

    @Test
    public void TEST_5() {
        try {
            ArgumentValidatorUtils.ensureGreaterThan(10, 11, "number", "especially number cannot be less than or equal to the limit");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than or equal to '11', additional information: especially number cannot be less than or equal to the limit")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureGreaterThan(10, 10, "number", "especially number cannot be less than or equal to the limit");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than or equal to '10', additional information: especially number cannot be less than or equal to the limit")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThan(10, 9, "number", "especially number cannot be less than or equal to the limit");

        try {
            ArgumentValidatorUtils.ensureGreaterThan(10, 11, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than or equal to '11'")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureGreaterThan(10, 10, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than or equal to '10'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThan(10, 9, "number");
    }

    @Test
    public void TEST_7() {
        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(-10D, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10.000000' cannot be less than '0.000000'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(0D, "number");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(10D, "number");
    }

    @Test
    public void TEST_8() {
        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(-10L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than '0'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(0L, "number");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(10L, "number");
    }

    @Test
    public void TEST_9() {
        try {
            ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(-10, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than '0'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(0, "number");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(10, "number");
    }

    @Test
    public void TEST_10() {
        try {
            ArgumentValidatorUtils.ensureGreaterThanZero(-10L, "number", "especially -10 is bad");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0', additional information: especially -10 is bad")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureGreaterThanZero(0L, "number", "especially 0 is bad");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0', additional information: especially 0 is bad")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanZero(10L, "number", "especially 10 is bad");

        try {
            ArgumentValidatorUtils.ensureGreaterThanZero(-10L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0'")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureGreaterThanZero(0L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanZero(10L, "number");
    }

    @Test
    public void TEST_12() {
        try {
            ArgumentValidatorUtils.ensureGreaterThanZero(-10, "number", "especially -10 is bad");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0', additional information: especially -10 is bad")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureGreaterThanZero(0, "number", "especially 0 is bad");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0', additional information: especially 0 is bad")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanZero(10, "number", "especially 10 is bad");

        try {
            ArgumentValidatorUtils.ensureGreaterThanZero(-10, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0'")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureGreaterThanZero(0, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureGreaterThanZero(10, "number");
    }

    @Test
    public void TEST_13() {
        try {
            ArgumentValidatorUtils.ensureLessThanOrEqualTo(10D, 9D, "number", "especially number cannot be greater than the limit");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10.000000' cannot be greater than '9.000000', additional information: especially number cannot be greater than the limit")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10D, 10D, "number", "especially number cannot be greater than the limit");
        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10D, 11D, "number", "especially number cannot be greater than the limit");

        try {
            ArgumentValidatorUtils.ensureLessThanOrEqualTo(10D, 9D, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10.000000' cannot be greater than '9.000000'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10D, 10D, "number");
        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10D, 11D, "number");
    }

    @Test
    public void TEST_17() {
        try {
            ArgumentValidatorUtils.ensureLessThanOrEqualTo(10L, 9L, "number", "especially number cannot be greater than the limit");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9', additional information: especially number cannot be greater than the limit")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10L, 10L, "number", "especially number cannot be greater than the limit");
        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10L, 11L, "number", "especially number cannot be greater than the limit");

        try {
            ArgumentValidatorUtils.ensureLessThanOrEqualTo(10L, 9L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10L, 10L, "number");
        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10L, 11L, "number");
    }

    @Test
    public void TEST_18() {
        try {
            ArgumentValidatorUtils.ensureLessThanOrEqualTo(10, 9, "number", "especially number cannot be greater than the limit");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9', additional information: especially number cannot be greater than the limit")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10, 10, "number", "especially number cannot be greater than the limit");
        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10, 11, "number", "especially number cannot be greater than the limit");

        try {
            ArgumentValidatorUtils.ensureLessThanOrEqualTo(10, 9, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10, 10, "number");
        ArgumentValidatorUtils.ensureLessThanOrEqualTo(10, 11, "number");
    }

    @Test
    public void TEST_21() {
        try {
            ArgumentValidatorUtils.ensureEqual(10L, 9L, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9', additional information: especially 10 of course")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureEqual(10L, 11L, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11', additional information: especially 10 of course")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureEqual(10L, 10L, "number", "especially 10 of course");

        try {
            ArgumentValidatorUtils.ensureEqual(10L, 9L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9'")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureEqual(10L, 11L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureEqual(10L, 10L, "number");
    }

    @Test
    public void TEST_22() {
        try {
            ArgumentValidatorUtils.ensureEqual(10, 9, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9', additional information: especially 10 of course")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureEqual(10, 11, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11', additional information: especially 10 of course")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureEqual(10, 10, "number", "especially 10 of course");

        try {
            ArgumentValidatorUtils.ensureEqual(10, 9, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9'")
                    .build(), ThrowableComparer.create(e));
        }

        try {
            ArgumentValidatorUtils.ensureEqual(10, 11, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11'")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureEqual(10, 10, "number");
    }

    @Test
    public void TEST_23() {
        try {
            ArgumentValidatorUtils.ensureFalse(true, "boolean", "especially true");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("boolean especially true")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureFalse(false, "boolean", "especially false");
    }

    @Test
    public void TEST_24() {
        try {
            ArgumentValidatorUtils.ensureTrue(false, "boolean", "especially false");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("boolean especially false")
                    .build(), ThrowableComparer.create(e));
        }

        ArgumentValidatorUtils.ensureTrue(true, "boolean", "especially true");
    }
}