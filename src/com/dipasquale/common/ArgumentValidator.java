package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArgumentValidator {
    @Getter
    private static final ArgumentValidator instance = new ArgumentValidator();

    public void ensureNotNull(final Object object, final String name) {
        if (object == null) {
            String message = String.format("%s cannot be null", name);

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureGreaterThanOrEqualTo(final double number, final double limit, final String name, final String additionalMessage) {
        if (Double.compare(number, limit) < 0) {
            String message = String.format("%s '%f' cannot be less than '%f'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureGreaterThanOrEqualTo(final double number, final double limit, final String name) {
        ensureGreaterThanOrEqualTo(number, limit, name, null);
    }

    public void ensureGreaterThanOrEqualTo(final long number, final long limit, final String name, final String additionalMessage) {
        if (number < limit) {
            String message = String.format("%s '%d' cannot be less than '%d'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureGreaterThanOrEqualTo(final long number, final long limit, final String name) {
        ensureGreaterThanOrEqualTo(number, limit, name, null);
    }

    public void ensureGreaterThanOrEqualTo(final int number, final int limit, final String name, final String additionalMessage) {
        ensureGreaterThanOrEqualTo((long) number, limit, name, additionalMessage);
    }

    public void ensureGreaterThanOrEqualTo(final int number, final int limit, final String name) {
        ensureGreaterThanOrEqualTo(number, limit, name, null);
    }

    public void ensureGreaterThan(final long number, final long limit, final String name, final String additionalMessage) {
        if (number <= limit) {
            String message = String.format("%s '%d' cannot be less than or equal to '%d'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureGreaterThan(final long number, final long limit, final String name) {
        ensureGreaterThan(number, limit, name, null);
    }

    public void ensureGreaterThan(final int number, final int limit, final String name, final String additionalMessage) {
        ensureGreaterThan((long) number, limit, name, additionalMessage);
    }

    public void ensureGreaterThan(final int number, final int limit, final String name) {
        ensureGreaterThan(number, limit, name, null);
    }

    public void ensureGreaterThanOrEqualToZero(final double number, final String name) {
        ensureGreaterThanOrEqualTo(number, 0D, name);
    }

    public void ensureGreaterThanOrEqualToZero(final long number, final String name) {
        ensureGreaterThanOrEqualTo(number, 0L, name);
    }

    public void ensureGreaterThanOrEqualToZero(final int number, final String name) {
        ensureGreaterThanOrEqualTo(number, 0, name);
    }

    public void ensureGreaterThanZero(final long number, final String name, final String additionalMessage) {
        ensureGreaterThan(number, 0L, name, additionalMessage);
    }

    public void ensureGreaterThanZero(final long number, final String name) {
        ensureGreaterThanZero(number, name, null);
    }

    public void ensureGreaterThanZero(final int number, final String name, final String additionalMessage) {
        ensureGreaterThanZero((long) number, name, additionalMessage);
    }

    public void ensureGreaterThanZero(final int number, final String name) {
        ensureGreaterThanZero(number, name, null);
    }

    public void ensureLessThanOrEqualTo(final double number, final double limit, final String name, final String additionalMessage) {
        if (number > limit) {
            String message = String.format("%s '%f' cannot be greater than '%f'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureLessThanOrEqualTo(final double number, final double limit, final String name) {
        ensureLessThanOrEqualTo(number, limit, name, null);
    }

    public void ensureLessThanOrEqualTo(final long number, final long limit, final String name, final String additionalMessage) {
        if (number > limit) {
            String message = String.format("%s '%d' cannot be greater than '%d'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureLessThanOrEqualTo(final long number, final long limit, final String name) {
        ensureLessThanOrEqualTo(number, limit, name, null);
    }

    public void ensureLessThanOrEqualTo(final int number, final int limit, final String name, final String additionalMessage) {
        ensureLessThanOrEqualTo((long) number, limit, name, additionalMessage);
    }

    public void ensureLessThanOrEqualTo(final int number, final int limit, final String name) {
        ensureLessThanOrEqualTo((long) number, limit, name, null);
    }

    public void ensureLessThan(final long number, final long limit, final String name, final String additionalMessage) {
        if (number >= limit) {
            String message = String.format("%s '%d' cannot be greater than or equal to '%d'", name, number, limit);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureLessThan(final long number, final long limit, final String name) {
        ensureLessThan(number, limit, name, null);
    }

    public void ensureLessThan(final int number, final int limit, final String name, final String additionalMessage) {
        ensureLessThan((long) number, limit, name, additionalMessage);
    }

    public void ensureLessThan(final int number, final int limit, final String name) {
        ensureLessThan((long) number, limit, name, null);
    }

    public void ensureEqual(final long number, final long target, final String name, final String additionalMessage) {
        if (number != target) {
            String message = String.format("%s '%d' cannot differ from '%d'", name, number, target);

            if (!StringUtils.isBlank(additionalMessage)) {
                message = String.format("%s, additional information: %s", message, additionalMessage);
            }

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureEqual(final long number, final long target, final String name) {
        ensureEqual(number, target, name, null);
    }

    public void ensureEqual(final int number, final int target, final String name, final String additionalMessage) {
        ensureEqual((long) number, target, name, additionalMessage);
    }

    public void ensureEqual(final int number, final int target, final String name) {
        ensureEqual((long) number, target, name, null);
    }

    public void ensureFalse(final boolean predicate, final String name, final String additionalMessage) {
        if (predicate) {
            String message = String.format("%s %s", name, additionalMessage);

            throw new IllegalArgumentException(message);
        }
    }

    public void ensureTrue(final boolean predicate, final String name, final String additionalMessage) {
        ensureFalse(!predicate, name, additionalMessage);
    }
}
