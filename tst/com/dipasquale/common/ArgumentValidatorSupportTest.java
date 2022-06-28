package com.dipasquale.common;

import com.dipasquale.common.error.ErrorSnapshot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ArgumentValidatorSupportTest {
    @Test
    public void TEST_1() {
        try {
            ArgumentValidatorSupport.ensureNotNull(null, "object");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("object cannot be null")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureNotNull(new Object(), "object");
    }

    @Test
    public void TEST_2() {
        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10D, 11D, "number", "especially number cannot be less than the limit");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10.000000' cannot be less than '11.000000', additional information: especially number cannot be less than the limit")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10D, 10D, "number", "especially number cannot be less than the limit");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10D, 9D, "number", "especially number cannot be less than the limit");

        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10D, 11D, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10.000000' cannot be less than '11.000000'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10D, 10D, "number");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10D, 9D, "number");
    }

    @Test
    public void TEST_3() {
        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10L, 11L, "number", "especially number cannot be less than the limit");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11', additional information: especially number cannot be less than the limit")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10L, 10L, "number", "especially number cannot be less than the limit");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10L, 9L, "number", "especially number cannot be less than the limit");

        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10L, 11L, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10L, 10L, "number");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10L, 9L, "number");
    }

    @Test
    public void TEST_4() {
        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10, 11, "number", "especially number cannot be less than the limit");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11', additional information: especially number cannot be less than the limit")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10, 10, "number", "especially number cannot be less than the limit");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10, 9, "number", "especially number cannot be less than the limit");

        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10, 11, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10, 10, "number");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(10, 9, "number");
    }

    @Test
    public void TEST_5() {
        try {
            ArgumentValidatorSupport.ensureGreaterThan(10, 11, "number", "especially number cannot be less than or equal to the limit");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than or equal to '11', additional information: especially number cannot be less than or equal to the limit")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureGreaterThan(10, 10, "number", "especially number cannot be less than or equal to the limit");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than or equal to '10', additional information: especially number cannot be less than or equal to the limit")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThan(10, 9, "number", "especially number cannot be less than or equal to the limit");

        try {
            ArgumentValidatorSupport.ensureGreaterThan(10, 11, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than or equal to '11'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureGreaterThan(10, 10, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than or equal to '10'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThan(10, 9, "number");
    }

    @Test
    public void TEST_7() {
        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(-10D, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10.000000' cannot be less than '0.000000'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(0D, "number");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(10D, "number");
    }

    @Test
    public void TEST_8() {
        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(-10L, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(0L, "number");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(10L, "number");
    }

    @Test
    public void TEST_9() {
        try {
            ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(-10, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(0, "number");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(10, "number");
    }

    @Test
    public void TEST_10() {
        try {
            ArgumentValidatorSupport.ensureGreaterThanZero(-10L, "number", "especially -10 is bad");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0', additional information: especially -10 is bad")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureGreaterThanZero(0L, "number", "especially 0 is bad");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0', additional information: especially 0 is bad")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanZero(10L, "number", "especially 10 is bad");

        try {
            ArgumentValidatorSupport.ensureGreaterThanZero(-10L, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureGreaterThanZero(0L, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanZero(10L, "number");
    }

    @Test
    public void TEST_12() {
        try {
            ArgumentValidatorSupport.ensureGreaterThanZero(-10, "number", "especially -10 is bad");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0', additional information: especially -10 is bad")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureGreaterThanZero(0, "number", "especially 0 is bad");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0', additional information: especially 0 is bad")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanZero(10, "number", "especially 10 is bad");

        try {
            ArgumentValidatorSupport.ensureGreaterThanZero(-10, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureGreaterThanZero(0, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureGreaterThanZero(10, "number");
    }

    @Test
    public void TEST_13() {
        try {
            ArgumentValidatorSupport.ensureLessThanOrEqualTo(10D, 9D, "number", "especially number cannot be greater than the limit");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10.000000' cannot be greater than '9.000000', additional information: especially number cannot be greater than the limit")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10D, 10D, "number", "especially number cannot be greater than the limit");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10D, 11D, "number", "especially number cannot be greater than the limit");

        try {
            ArgumentValidatorSupport.ensureLessThanOrEqualTo(10D, 9D, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10.000000' cannot be greater than '9.000000'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10D, 10D, "number");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10D, 11D, "number");
    }

    @Test
    public void TEST_17() {
        try {
            ArgumentValidatorSupport.ensureLessThanOrEqualTo(10L, 9L, "number", "especially number cannot be greater than the limit");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9', additional information: especially number cannot be greater than the limit")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10L, 10L, "number", "especially number cannot be greater than the limit");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10L, 11L, "number", "especially number cannot be greater than the limit");

        try {
            ArgumentValidatorSupport.ensureLessThanOrEqualTo(10L, 9L, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10L, 10L, "number");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10L, 11L, "number");
    }

    @Test
    public void TEST_18() {
        try {
            ArgumentValidatorSupport.ensureLessThanOrEqualTo(10, 9, "number", "especially number cannot be greater than the limit");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9', additional information: especially number cannot be greater than the limit")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10, 10, "number", "especially number cannot be greater than the limit");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10, 11, "number", "especially number cannot be greater than the limit");

        try {
            ArgumentValidatorSupport.ensureLessThanOrEqualTo(10, 9, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10, 10, "number");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(10, 11, "number");
    }

    @Test
    public void TEST_21() {
        try {
            ArgumentValidatorSupport.ensureEqual(10L, 9L, "number", "especially 10 of course");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9', additional information: especially 10 of course")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureEqual(10L, 11L, "number", "especially 10 of course");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11', additional information: especially 10 of course")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureEqual(10L, 10L, "number", "especially 10 of course");

        try {
            ArgumentValidatorSupport.ensureEqual(10L, 9L, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureEqual(10L, 11L, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureEqual(10L, 10L, "number");
    }

    @Test
    public void TEST_22() {
        try {
            ArgumentValidatorSupport.ensureEqual(10, 9, "number", "especially 10 of course");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9', additional information: especially 10 of course")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureEqual(10, 11, "number", "especially 10 of course");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11', additional information: especially 10 of course")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureEqual(10, 10, "number", "especially 10 of course");

        try {
            ArgumentValidatorSupport.ensureEqual(10, 9, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            ArgumentValidatorSupport.ensureEqual(10, 11, "number");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11'")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureEqual(10, 10, "number");
    }

    @Test
    public void TEST_23() {
        try {
            ArgumentValidatorSupport.ensureFalse(true, "boolean", "especially true");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("boolean especially true")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureFalse(false, "boolean", "especially false");
    }

    @Test
    public void TEST_24() {
        try {
            ArgumentValidatorSupport.ensureTrue(false, "boolean", "especially false");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("boolean especially false")
                    .build(), ErrorSnapshot.create(e));
        }

        ArgumentValidatorSupport.ensureTrue(true, "boolean", "especially true");
    }
}
