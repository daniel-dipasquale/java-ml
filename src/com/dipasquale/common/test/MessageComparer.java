package com.dipasquale.common.test;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Generated
@RequiredArgsConstructor
final class MessageComparer {
    private final String value;
    private final Pattern pattern;

    static MessageComparer literal(final String message) {
        return new MessageComparer(message, null);
    }

    static MessageComparer pattern(final Pattern pattern) {
        return new MessageComparer(null, pattern);
    }

    private static boolean equals(final String message, final Pattern regex) {
        return regex.matcher(message).matches();
    }

    private boolean equals(final MessageComparer other) {
        String message = Optional.ofNullable(value)
                .orElse(other.value);

        Pattern regex = Optional.ofNullable(pattern)
                .orElse(other.pattern);

        if (message != null && regex != null) {
            return equals(message, regex);
        }

        if (regex != null) {
            String pattern1 = Optional.ofNullable(pattern)
                    .map(Pattern::pattern)
                    .orElse(null);

            String pattern2 = Optional.ofNullable(other.pattern)
                    .map(Pattern::pattern)
                    .orElse(null);

            return StringUtils.equals(pattern1, pattern2);
        }

        return StringUtils.equals(value, other.value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return equals((MessageComparer) o);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // NOTE: not great for performance, but that's also not a concern
    }

    @Override
    public String toString() {
        if (pattern == null) {
            return value;
        }

        return pattern.pattern();
    }
}
