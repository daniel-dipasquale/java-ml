package com.dipasquale.common;

import com.dipasquale.common.test.ThrowableAsserter;
import org.junit.Assert;
import org.junit.Test;

public final class ArgumentValidatorTest {
    @Test
    public void TEST_1() {
        try {
            ArgumentValidator.getInstance().ensureNotNull(null, "object");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("object cannot be null")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureNotNull(new Object(), "object");
    }

    @Test
    public void TEST_2() {
        try {
            ArgumentValidator.getInstance().ensureGreaterThanZero(-10L, "number", "especially -10 is bad");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0', additional information: especially -10 is bad")
                    .build(), ThrowableAsserter.create(e));
        }

        try {
            ArgumentValidator.getInstance().ensureGreaterThanZero(0L, "number", "especially 0 is bad");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0', additional information: especially 0 is bad")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureGreaterThanZero(10L, "number", "especially -10 is bad");
    }

    @Test
    public void TEST_3() {
        try {
            ArgumentValidator.getInstance().ensureGreaterThanZero(-10L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0'")
                    .build(), ThrowableAsserter.create(e));
        }

        try {
            ArgumentValidator.getInstance().ensureGreaterThanZero(0L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureGreaterThanZero(10L, "number");
    }

    @Test
    public void TEST_4() {
        try {
            ArgumentValidator.getInstance().ensureGreaterThanZero(-10, "number", "especially -10 is bad");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0', additional information: especially -10 is bad")
                    .build(), ThrowableAsserter.create(e));
        }

        try {
            ArgumentValidator.getInstance().ensureGreaterThanZero(0, "number", "especially 0 is bad");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0', additional information: especially 0 is bad")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureGreaterThanZero(10, "number", "especially -10 is bad");
    }

    @Test
    public void TEST_5() {
        try {
            ArgumentValidator.getInstance().ensureGreaterThanZero(-10, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than or equal to '0'")
                    .build(), ThrowableAsserter.create(e));
        }

        try {
            ArgumentValidator.getInstance().ensureGreaterThanZero(0, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '0' cannot be less than or equal to '0'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureGreaterThanZero(10, "number");
    }

    @Test
    public void TEST_6() {
        try {
            ArgumentValidator.getInstance().ensureGreaterThanOrEqualToZero(-10L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than '0'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureGreaterThanOrEqualToZero(10L, "number");
        ArgumentValidator.getInstance().ensureGreaterThanOrEqualToZero(0L, "number");
    }

    @Test
    public void TEST_7() {
        try {
            ArgumentValidator.getInstance().ensureGreaterThanOrEqualToZero(-10, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '-10' cannot be less than '0'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureGreaterThanOrEqualToZero(10, "number");
        ArgumentValidator.getInstance().ensureGreaterThanOrEqualToZero(0, "number");
    }

    @Test
    public void TEST_8() {
        try {
            ArgumentValidator.getInstance().ensureGreaterThanOrEqualTo(10, 11, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be less than '11'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureGreaterThanOrEqualTo(10, 10, "number");
        ArgumentValidator.getInstance().ensureGreaterThanOrEqualTo(10, 0, "number");
    }

    @Test
    public void TEST_9() {
        try {
            ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10L, 9L, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9', additional information: especially 10 of course")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10L, 10L, "number", "especially 10 of course");
        ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10L, 11L, "number", "especially 10 of course");
    }

    @Test
    public void TEST_10() {
        try {
            ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10L, 9L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10L, 10L, "number");
        ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10L, 11L, "number");
    }

    @Test
    public void TEST_11() {
        try {
            ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10, 9, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9', additional information: especially 10 of course")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10, 10, "number", "especially 10 of course");
        ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10, 11, "number", "especially 10 of course");
    }

    @Test
    public void TEST_12() {
        try {
            ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10, 9, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot be greater than '9'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10, 10, "number");
        ArgumentValidator.getInstance().ensureLessThanOrEqualTo(10, 11, "number");
    }

    @Test
    public void TEST_13() {
        try {
            ArgumentValidator.getInstance().ensureEqual(10L, 9L, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9', additional information: especially 10 of course")
                    .build(), ThrowableAsserter.create(e));
        }

        try {
            ArgumentValidator.getInstance().ensureEqual(10L, 11L, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11', additional information: especially 10 of course")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureEqual(10L, 10L, "number", "especially 10 of course");
    }

    @Test
    public void TEST_14() {
        try {
            ArgumentValidator.getInstance().ensureEqual(10L, 9L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9'")
                    .build(), ThrowableAsserter.create(e));
        }

        try {
            ArgumentValidator.getInstance().ensureEqual(10L, 11L, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureEqual(10L, 10L, "number");
    }

    @Test
    public void TEST_15() {
        try {
            ArgumentValidator.getInstance().ensureEqual(10, 9, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9', additional information: especially 10 of course")
                    .build(), ThrowableAsserter.create(e));
        }

        try {
            ArgumentValidator.getInstance().ensureEqual(10, 11, "number", "especially 10 of course");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11', additional information: especially 10 of course")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureEqual(10, 10, "number", "especially 10 of course");
    }

    @Test
    public void TEST_16() {
        try {
            ArgumentValidator.getInstance().ensureEqual(10, 9, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '9'")
                    .build(), ThrowableAsserter.create(e));
        }

        try {
            ArgumentValidator.getInstance().ensureEqual(10, 11, "number");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("number '10' cannot differ from '11'")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureEqual(10, 10, "number");
    }

    @Test
    public void TEST_17() {
        try {
            ArgumentValidator.getInstance().ensureFalse(true, "boolean", "especially boolean");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("boolean especially boolean")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureFalse(false, "boolean", "especially boolean");
    }

    @Test
    public void TEST_18() {
        try {
            ArgumentValidator.getInstance().ensureTrue(false, "boolean", "especially boolean");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("boolean especially boolean")
                    .build(), ThrowableAsserter.create(e));
        }

        ArgumentValidator.getInstance().ensureTrue(true, "boolean", "especially boolean");
    }
}
