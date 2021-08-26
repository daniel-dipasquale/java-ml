package com.dipasquale.synchronization.dual.profile;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serial;

@Getter(AccessLevel.PROTECTED)
public final class DefaultObjectProfile<T> extends AbstractObjectProfile<T> {
    @Serial
    private static final long serialVersionUID = -4711611220090246081L;

    public DefaultObjectProfile(final boolean concurrent, final T concurrentObject, final T defaultObject) {
        super(concurrent, concurrentObject, defaultObject);
    }

    public DefaultObjectProfile(final boolean concurrent, final T object) {
        this(concurrent, object, object);
    }
}