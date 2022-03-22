package com.dipasquale.common.concurrent;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class ReferenceContainer<T> {
    private final T reference;
    private final RuntimeException exception;
    private final long expirationDateTime;
}
