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
import java.util.stream.Collectors;

@Generated // TODO: should the testing tool be tested? I'm feeling like it should be
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public final class ThrowableComparer {
    private final Class<?> type;
    private final Message message;
    private final ThrowableComparer cause;
    private final List<ThrowableComparer> suppressed;

    @Builder
    private static ThrowableComparer create(final Class<?> type, final String messagePattern, final String message, final ThrowableComparer cause, final List<ThrowableComparer> suppressed) {
        Message messageArg = Optional.ofNullable(messagePattern)
                .map(Message::pattern)
                .orElseGet(() -> Message.literal(message));

        List<ThrowableComparer> suppressedArg = Optional.ofNullable(suppressed)
                .orElseGet(ImmutableList::of);

        return new ThrowableComparer(type, messageArg, cause, suppressedArg);
    }

    public static ThrowableComparer create(final Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        Class<?> type = throwable.getClass();
        Message message = Message.literal(throwable.getMessage());
        ThrowableComparer cause = create(throwable.getCause());

        List<ThrowableComparer> suppressed = Arrays.stream(throwable.getSuppressed())
                .map(ThrowableComparer::create)
                .collect(Collectors.toList());

        return new ThrowableComparer(type, message, cause, suppressed);
    }
}
