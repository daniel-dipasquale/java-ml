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
public final class ErrorComparator {
    private final Class<? extends Throwable> type;
    private final MessageComparator message;
    private final ErrorComparator cause;
    private final List<ErrorComparator> suppressed;
    private final FieldComparatorGroup<? extends Throwable> fields;

    private static Optional<MessageComparator> createMessagePattern(final Object message) {
        return Optional.ofNullable(message)
                .filter(m -> m instanceof Pattern)
                .map(m -> (Pattern) m)
                .map(MessageComparator::pattern);
    }

    private static Optional<MessageComparator> createMessageLiteral(final Object message) {
        return Optional.ofNullable(message)
                .filter(m -> m instanceof String)
                .map(m -> (String) m)
                .map(MessageComparator::literal);
    }

    @Builder
    private static <T extends Throwable> ErrorComparator create(final Class<? extends T> type,
                                                                final Object message,
                                                                final ErrorComparator cause,
                                                                final List<ErrorComparator> suppressed,
                                                                final List<FieldComparator<T>> fields) {
        MessageComparator messageFixed = createMessagePattern(message)
                .orElseGet(() -> createMessageLiteral(message).orElse(null));

        List<ErrorComparator> suppressedFixed = Optional.ofNullable(suppressed)
                .orElseGet(ImmutableList::of);

        FieldComparatorGroup<T> fieldsFixed = Optional.ofNullable(fields)
                .map(f -> new FieldComparatorGroup<>(null, f))
                .orElseGet(() -> new FieldComparatorGroup<>(null, new ArrayList<>()));

        return new ErrorComparator(type, messageFixed, cause, suppressedFixed, fieldsFixed);
    }

    public static <T extends Throwable> ErrorComparator create(final T error) {
        if (error == null) {
            return null;
        }

        Class<? extends Throwable> type = error.getClass();
        MessageComparator message = MessageComparator.literal(error.getMessage());
        ErrorComparator cause = create(error.getCause());

        List<ErrorComparator> suppressed = Arrays.stream(error.getSuppressed())
                .map(ErrorComparator::create)
                .collect(Collectors.toList());

        FieldComparatorGroup<T> fields = new FieldComparatorGroup<>(error, new ArrayList<>());

        return new ErrorComparator(type, message, cause, suppressed, fields);
    }
}
