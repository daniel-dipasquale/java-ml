package com.dipasquale.common.test;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Generated // TODO: should the testing tool be tested? I'm feeling like it should be
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public final class ThrowableAsserter {
    private final Class<?> type;
    private final Message message;
    private final ThrowableAsserter cause;
    private final List<ThrowableAsserter> suppressed;

    @Builder
    private static ThrowableAsserter create(final Class<?> type, final String messagePattern, final String message, final ThrowableAsserter cause, final List<ThrowableAsserter> suppressed) {
        Message messageArg = Optional.ofNullable(messagePattern)
                .map(Message::pattern)
                .orElseGet(() -> Message.literal(message));

        List<ThrowableAsserter> suppressedArg = Optional.ofNullable(suppressed)
                .orElseGet(ImmutableList::of);

        return new ThrowableAsserter(type, messageArg, cause, suppressedArg);
    }

    public static ThrowableAsserter create(final Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        Class<?> type = throwable.getClass();
        Message message = Message.literal(throwable.getMessage());
        ThrowableAsserter cause = create(throwable.getCause());

        List<ThrowableAsserter> suppressed = Arrays.stream(throwable.getSuppressed())
                .map(ThrowableAsserter::create)
                .collect(Collectors.toList());

        return new ThrowableAsserter(type, message, cause, suppressed);
    }

    @Generated
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Message {
        private final String value;
        private final Pattern pattern;

        private static Message literal(final String message) {
            return new Message(message, null);
        }

        private static Message pattern(final String pattern) {
            return new Message(null, Pattern.compile(pattern));
        }

        private static boolean equals(final String message, final Pattern regex) {
            return regex.matcher(message).matches();
        }

        private boolean equals(final Message other) {
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

            return equals((Message) o);
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
}
