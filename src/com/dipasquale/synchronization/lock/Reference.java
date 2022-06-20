package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class Reference<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 4376858709182600777L;
    private final Object token;
    private final T value;

    public static <T> Reference<T> create(final RcuController controller, final T reference) {
        return new Reference<>(controller.getState().getToken(), reference);
    }
}
