package com.dipasquale.common.test;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public final class ThrowableComparer {
    private final Class<?> type;
    private final MessageComparer message;
    private final ThrowableComparer cause;
    private final List<ThrowableComparer> suppressed;

    private static Optional<MessageComparer> createMessagePattern(final Object message) {
        return Optional.ofNullable(message)
                .filter(m -> m instanceof Pattern)
                .map(m -> (Pattern) m)
                .map(MessageComparer::pattern);
    }

    private static Optional<MessageComparer> createMessageLiteral(final Object message) {
        return Optional.ofNullable(message)
                .filter(m -> m instanceof String)
                .map(m -> (String) m)
                .map(MessageComparer::literal);
    }

    @Builder
    private static ThrowableComparer create(final Class<?> type, final Object message, final ThrowableComparer cause, final List<ThrowableComparer> suppressed) {
        MessageComparer messageArg = createMessagePattern(message)
                .orElseGet(() -> createMessageLiteral(message).orElse(null));

        List<ThrowableComparer> suppressedArg = Optional.ofNullable(suppressed)
                .orElseGet(ImmutableList::of);

        return new ThrowableComparer(type, messageArg, cause, suppressedArg);
    }

    public static ThrowableComparer create(final Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        Class<?> type = throwable.getClass();
        MessageComparer message = MessageComparer.literal(throwable.getMessage());
        ThrowableComparer cause = create(throwable.getCause());

        List<ThrowableComparer> suppressed = Arrays.stream(throwable.getSuppressed())
                .map(ThrowableComparer::create)
                .collect(Collectors.toList());

        return new ThrowableComparer(type, message, cause, suppressed);
    }
}
