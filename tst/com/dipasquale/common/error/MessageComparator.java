package com.dipasquale.common.error;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class MessageComparator {
    private final String value;
    private final Pattern pattern;

    static MessageComparator literal(final String message) {
        return new MessageComparator(message, null);
    }

    static MessageComparator pattern(final Pattern pattern) {
        return new MessageComparator(null, pattern);
    }

    private static boolean equals(final String message, final Pattern regex) {
        return regex.matcher(message).matches();
    }

    private boolean equals(final MessageComparator other) {
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

            return Objects.equals(pattern1, pattern2);
        }

        return Objects.equals(value, other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        return equals((MessageComparator) other);
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
