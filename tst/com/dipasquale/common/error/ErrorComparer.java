/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.error;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
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
public final class ErrorComparer {
    private final Class<? extends Throwable> type;
    private final MessageComparer message;
    private final ErrorComparer cause;
    private final List<ErrorComparer> suppressed;
    private final FieldComparerCollection<? extends Throwable> fields;

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
    private static <T extends Throwable> ErrorComparer create(final Class<? extends T> type,
                                                              final Object message,
                                                              final ErrorComparer cause,
                                                              final List<ErrorComparer> suppressed,
                                                              final List<FieldComparer<T>> fields) {
        MessageComparer messageFixed = createMessagePattern(message)
                .orElseGet(() -> createMessageLiteral(message).orElse(null));

        List<ErrorComparer> suppressedFixed = Optional.ofNullable(suppressed)
                .orElseGet(ImmutableList::of);

        FieldComparerCollection<T> fieldsFixed = Optional.ofNullable(fields)
                .map(f -> new FieldComparerCollection<>(null, f))
                .orElseGet(() -> new FieldComparerCollection<>(null, new ArrayList<>()));

        return new ErrorComparer(type, messageFixed, cause, suppressedFixed, fieldsFixed);
    }

    public static <T extends Throwable> ErrorComparer create(final T error) {
        if (error == null) {
            return null;
        }

        Class<? extends Throwable> type = error.getClass();
        MessageComparer message = MessageComparer.literal(error.getMessage());
        ErrorComparer cause = create(error.getCause());

        List<ErrorComparer> suppressed = Arrays.stream(error.getSuppressed())
                .map(ErrorComparer::create)
                .collect(Collectors.toList());

        FieldComparerCollection<T> fields = new FieldComparerCollection<>(error, new ArrayList<>());

        return new ErrorComparer(type, message, cause, suppressed, fields);
    }
}
