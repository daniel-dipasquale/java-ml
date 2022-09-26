package com.dipasquale.common.error;

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
public final class ErrorSnapshot {
    private final Class<? extends Throwable> type;
    private final MessageSnapshot message;
    private final ErrorSnapshot cause;
    private final List<ErrorSnapshot> suppressed;
    private final FieldSnapshotGroup<? extends Throwable> fields;

    private static Optional<MessageSnapshot> createPatternMessageSnapshot(final Object message) {
        return Optional.ofNullable(message)
                .filter(__message -> __message instanceof Pattern)
                .map(__message -> (Pattern) __message)
                .map(MessageSnapshot::pattern);
    }

    private static Optional<MessageSnapshot> createExactMessageSnapshot(final Object message) {
        return Optional.ofNullable(message)
                .filter(__message -> __message instanceof String)
                .map(__message -> (String) __message)
                .map(MessageSnapshot::message);
    }

    @Builder
    private static <T extends Throwable> ErrorSnapshot create(final Class<? extends T> type, final Object message, final ErrorSnapshot cause, final List<ErrorSnapshot> suppressed, final List<FieldSnapshot<T>> fields) {
        MessageSnapshot fixedMessage = createPatternMessageSnapshot(message)
                .orElseGet(() -> createExactMessageSnapshot(message).orElse(null));

        List<ErrorSnapshot> fixedSuppressed = Optional.ofNullable(suppressed)
                .orElseGet(List::of);

        FieldSnapshotGroup<T> fixedFields = Optional.ofNullable(fields)
                .map(fieldComparators -> new FieldSnapshotGroup<>(null, fieldComparators))
                .orElseGet(() -> new FieldSnapshotGroup<>(null, new ArrayList<>()));

        return new ErrorSnapshot(type, fixedMessage, cause, fixedSuppressed, fixedFields);
    }

    public static <T extends Throwable> ErrorSnapshot create(final T error) {
        if (error == null) {
            return null;
        }

        Class<? extends Throwable> type = error.getClass();
        MessageSnapshot message = MessageSnapshot.message(error.getMessage());
        ErrorSnapshot cause = create(error.getCause());

        List<ErrorSnapshot> suppressed = Arrays.stream(error.getSuppressed())
                .map(ErrorSnapshot::create)
                .collect(Collectors.toList());

        FieldSnapshotGroup<T> fields = new FieldSnapshotGroup<>(error, new ArrayList<>());

        return new ErrorSnapshot(type, message, cause, suppressed, fields);
    }
}
