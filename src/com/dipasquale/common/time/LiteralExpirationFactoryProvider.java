/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.time;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class LiteralExpirationFactoryProvider implements ExpirationFactoryProvider, Serializable {
    @Serial
    private static final long serialVersionUID = -6266567359554482126L;
    private final ExpirationFactory expirationFactory;

    @Override
    public ExpirationFactory get(final int index) {
        return expirationFactory;
    }

    @Override
    public int size() {
        return 1;
    }
}
