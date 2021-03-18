package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArgumentValidatorUtils {
    public static void ensureNotNull(final Object object, final String name) {
        if (object == null) {
            String message = String.format("%s cannot be null", name);

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureGreaterThanOrEqualTo(final double number, final double limit, final String name, final String additionalMessage) {
        if (Double.compare(number, limit) < 0) {
            String message = String.format("%s '%f' cannot be less than '%f'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureGreaterThanOrEqualTo(final double number, final double limit, final String name) {
        ensureGreaterThanOrEqualTo(number, limit, name, null);
    }

    public static void ensureGreaterThanOrEqualTo(final long number, final long limit, final String name, final String additionalMessage) {
        if (number < limit) {
            String message = String.format("%s '%d' cannot be less than '%d'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureGreaterThanOrEqualTo(final long number, final long limit, final String name) {
        ensureGreaterThanOrEqualTo(number, limit, name, null);
    }

    public static void ensureGreaterThanOrEqualTo(final int number, final int limit, final String name, final String additionalMessage) {
        ensureGreaterThanOrEqualTo((long) number, limit, name, additionalMessage);
    }

    public static void ensureGreaterThanOrEqualTo(final int number, final int limit, final String name) {
        ensureGreaterThanOrEqualTo(number, limit, name, null);
    }

    public static void ensureGreaterThan(final long number, final long limit, final String name, final String additionalMessage) {
        if (number <= limit) {
            String message = String.format("%s '%d' cannot be less than or equal to '%d'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureGreaterThan(final long number, final long limit, final String name) {
        ensureGreaterThan(number, limit, name, null);
    }

    public static void ensureGreaterThan(final int number, final int limit, final String name, final String additionalMessage) {
        ensureGreaterThan((long) number, limit, name, additionalMessage);
    }

    public static void ensureGreaterThan(final int number, final int limit, final String name) {
        ensureGreaterThan(number, limit, name, null);
    }

    public static void ensureGreaterThanOrEqualToZero(final double number, final String name) {
        ensureGreaterThanOrEqualTo(number, 0D, name);
    }

    public static void ensureGreaterThanOrEqualToZero(final long number, final String name) {
        ensureGreaterThanOrEqualTo(number, 0L, name);
    }

    public static void ensureGreaterThanOrEqualToZero(final int number, final String name) {
        ensureGreaterThanOrEqualTo(number, 0, name);
    }

    public static void ensureGreaterThanZero(final long number, final String name, final String additionalMessage) {
        ensureGreaterThan(number, 0L, name, additionalMessage);
    }

    public static void ensureGreaterThanZero(final long number, final String name) {
        ensureGreaterThanZero(number, name, null);
    }

    public static void ensureGreaterThanZero(final int number, final String name, final String additionalMessage) {
        ensureGreaterThanZero((long) number, name, additionalMessage);
    }

    public static void ensureGreaterThanZero(final int number, final String name) {
        ensureGreaterThanZero(number, name, null);
    }

    public static void ensureLessThanOrEqualTo(final double number, final double limit, final String name, final String additionalMessage) {
        if (number > limit) {
            String message = String.format("%s '%f' cannot be greater than '%f'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureLessThanOrEqualTo(final double number, final double limit, final String name) {
        ensureLessThanOrEqualTo(number, limit, name, null);
    }

    public static void ensureLessThanOrEqualTo(final long number, final long limit, final String name, final String additionalMessage) {
        if (number > limit) {
            String message = String.format("%s '%d' cannot be greater than '%d'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureLessThanOrEqualTo(final long number, final long limit, final String name) {
        ensureLessThanOrEqualTo(number, limit, name, null);
    }

    public static void ensureLessThanOrEqualTo(final int number, final int limit, final String name, final String additionalMessage) {
        ensureLessThanOrEqualTo((long) number, limit, name, additionalMessage);
    }

    public static void ensureLessThanOrEqualTo(final int number, final int limit, final String name) {
        ensureLessThanOrEqualTo((long) number, limit, name, null);
    }

    public static void ensureLessThan(final long number, final long limit, final String name, final String additionalMessage) {
        if (number >= limit) {
            String message = String.format("%s '%d' cannot be greater than or equal to '%d'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureLessThan(final long number, final long limit, final String name) {
        ensureLessThan(number, limit, name, null);
    }

    public static void ensureLessThan(final int number, final int limit, final String name, final String additionalMessage) {
        ensureLessThan((long) number, limit, name, additionalMessage);
    }

    public static void ensureLessThan(final int number, final int limit, final String name) {
        ensureLessThan((long) number, limit, name, null);
    }

    public static void ensureEqual(final long number, final long target, final String name, final String additionalMessage) {
        if (number != target) {
            String message = String.format("%s '%d' cannot differ from '%d'", name, number, target);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureEqual(final long number, final long target, final String name) {
        ensureEqual(number, target, name, null);
    }

    public static void ensureEqual(final int number, final int target, final String name, final String additionalMessage) {
        ensureEqual((long) number, target, name, additionalMessage);
    }

    public static void ensureEqual(final int number, final int target, final String name) {
        ensureEqual((long) number, target, name, null);
    }

    public static void ensureFalse(final boolean predicate, final String name, final String additionalMessage) {
        if (predicate) {
            String message = String.format("%s %s", name, additionalMessage);

            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureTrue(final boolean predicate, final String name, final String additionalMessage) {
        ensureFalse(!predicate, name, additionalMessage);
    }
}
