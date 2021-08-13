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
public final class StaggeringExpirationFactoryProvider implements ExpirationFactoryProvider, Serializable {
    @Serial
    private static final long serialVersionUID = 36244956388583613L;
    private final ExpirationFactory.Creator expirationFactoryCreator;
    private final long expirationTime;
    private final int size;

    @Override
    public ExpirationFactory get(final int index) {
        return expirationFactoryCreator.create(expirationTime * (long) size, expirationTime * (long) index);
    }

    @Override
    public int size() {
        return size;
    }
}
