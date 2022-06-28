package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class RcuReference<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 4376858709182600777L;
    private final Object token;
    private final T value;
}
