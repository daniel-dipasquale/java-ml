package com.dipasquale.common;

import java.util.Objects;

public interface ArgumentValidatorSupport {
    private static boolean isBlank(final String value) {
        return value == null || value.isEmpty();
    }

    static void ensureGreaterThanOrEqualTo(final double actual, final double expected, final String name, final String additionalMessage) {
        if (Double.compare(actual, expected) < 0) {
            String message = String.format("%s '%f' cannot be less than '%f'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureGreaterThanOrEqualTo(final double actual, final double expected, final String name) {
        ensureGreaterThanOrEqualTo(actual, expected, name, null);
    }

    static void ensureGreaterThanOrEqualTo(final long actual, final long expected, final String name, final String additionalMessage) {
        if (actual < expected) {
            String message = String.format("%s '%d' cannot be less than '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureGreaterThanOrEqualTo(final long actual, final long expected, final String name) {
        ensureGreaterThanOrEqualTo(actual, expected, name, null);
    }

    static void ensureGreaterThanOrEqualTo(final int actual, final int expected, final String name, final String additionalMessage) {
        if (actual < expected) {
            String message = String.format("%s '%d' cannot be less than '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureGreaterThanOrEqualTo(final int actual, final int expected, final String name) {
        ensureGreaterThanOrEqualTo(actual, expected, name, null);
    }

    static void ensureGreaterThan(final double actual, final double expected, final String name, final String additionalMessage) {
        if (Double.compare(actual, expected) <= 0) {
            String message = String.format("%s '%f' cannot be less than or equal to '%f'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureGreaterThan(final double actual, final double expected, final String name) {
        ensureGreaterThan(actual, expected, name, null);
    }

    static void ensureGreaterThan(final long actual, final long expected, final String name, final String additionalMessage) {
        if (actual <= expected) {
            String message = String.format("%s '%d' cannot be less than or equal to '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureGreaterThan(final long actual, final long expected, final String name) {
        ensureGreaterThan(actual, expected, name, null);
    }

    static void ensureGreaterThan(final int actual, final int expected, final String name, final String additionalMessage) {
        if (actual <= expected) {
            String message = String.format("%s '%d' cannot be less than or equal to '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureGreaterThan(final int actual, final int expected, final String name) {
        ensureGreaterThan(actual, expected, name, null);
    }

    static void ensureGreaterThanOrEqualToZero(final double actual, final String name) {
        ensureGreaterThanOrEqualTo(actual, 0D, name);
    }

    static void ensureGreaterThanOrEqualToZero(final long actual, final String name) {
        ensureGreaterThanOrEqualTo(actual, 0L, name);
    }

    static void ensureGreaterThanOrEqualToZero(final int actual, final String name) {
        ensureGreaterThanOrEqualTo(actual, 0, name);
    }

    static void ensureGreaterThanZero(final double actual, final String name, final String additionalMessage) {
        ensureGreaterThan(actual, 0D, name, additionalMessage);
    }

    static void ensureGreaterThanZero(final double actual, final String name) {
        ensureGreaterThanZero(actual, name, null);
    }

    static void ensureGreaterThanZero(final long actual, final String name, final String additionalMessage) {
        ensureGreaterThan(actual, 0L, name, additionalMessage);
    }

    static void ensureGreaterThanZero(final long actual, final String name) {
        ensureGreaterThanZero(actual, name, null);
    }

    static void ensureGreaterThanZero(final int actual, final String name, final String additionalMessage) {
        ensureGreaterThan(actual, 0, name, additionalMessage);
    }

    static void ensureGreaterThanZero(final int actual, final String name) {
        ensureGreaterThanZero(actual, name, null);
    }

    static void ensureLessThanOrEqualTo(final double actual, final double expected, final String name, final String additionalMessage) {
        if (Double.compare(actual, expected) > 0) {
            String message = String.format("%s '%f' cannot be greater than '%f'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureLessThanOrEqualTo(final double actual, final double expected, final String name) {
        ensureLessThanOrEqualTo(actual, expected, name, null);
    }

    static void ensureLessThanOrEqualTo(final long actual, final long expected, final String name, final String additionalMessage) {
        if (actual > expected) {
            String message = String.format("%s '%d' cannot be greater than '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureLessThanOrEqualTo(final long actual, final long expected, final String name) {
        ensureLessThanOrEqualTo(actual, expected, name, null);
    }

    static void ensureLessThanOrEqualTo(final int actual, final int expected, final String name, final String additionalMessage) {
        if (actual > expected) {
            String message = String.format("%s '%d' cannot be greater than '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureLessThanOrEqualTo(final int actual, final int expected, final String name) {
        ensureLessThanOrEqualTo(actual, expected, name, null);
    }

    static void ensureLessThan(final long actual, final long expected, final String name, final String additionalMessage) {
        if (actual >= expected) {
            String message = String.format("%s '%d' cannot be greater than or equal to '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureLessThan(final long actual, final long expected, final String name) {
        ensureLessThan(actual, expected, name, null);
    }

    static void ensureLessThan(final float actual, final float expected, final String name, final String additionalMessage) {
        if (Float.compare(actual, expected) >= 0) {
            String message = String.format("%s '%f' cannot be greater than or equal to '%f'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureLessThan(final float actual, final float expected, final String name) {
        ensureLessThan(actual, expected, name, null);
    }

    static void ensureLessThan(final int actual, final int expected, final String name, final String additionalMessage) {
        if (actual >= expected) {
            String message = String.format("%s '%d' cannot be greater than or equal to '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureLessThan(final int actual, final int expected, final String name) {
        ensureLessThan(actual, expected, name, null);
    }

    static void ensureEqual(final long actual, final long expected, final String name, final String additionalMessage) {
        if (actual != expected) {
            String message = String.format("%s '%d' cannot differ from '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureEqual(final long actual, final long expected, final String name) {
        ensureEqual(actual, expected, name, null);
    }

    static void ensureEqual(final int actual, final int expected, final String name, final String additionalMessage) {
        if (actual != expected) {
            String message = String.format("%s '%d' cannot differ from '%d'", name, actual, expected);

            if (!isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureEqual(final int actual, final int expected, final String name) {
        ensureEqual(actual, expected, name, null);
    }

    static void ensureEqual(final String actual, final String expected, final String name, final String additionalMessage) {
        if (!Objects.equals(actual, expected)) {
            String message = String.format("%s %s", name, additionalMessage);

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureFalse(final boolean actual, final String name, final String additionalMessage) {
        if (actual) {
            String message = String.format("%s %s", name, additionalMessage);

            throw new IllegalArgumentException(message);
        }
    }

    static void ensureTrue(final boolean actual, final String name, final String additionalMessage) {
        ensureFalse(!actual, name, additionalMessage);
    }

    static void ensureNotNull(final Object actual, final String name) {
        if (actual == null) {
            String message = String.format("%s cannot be null", name);

            throw new IllegalArgumentException(message);
        }
    }
}
