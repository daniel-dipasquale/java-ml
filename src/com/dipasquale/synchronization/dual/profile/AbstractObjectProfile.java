package com.dipasquale.synchronization.dual.profile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractObjectProfile<T> implements ObjectProfile<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -2031619467740284195L;
    private boolean parallel;
    @Getter(AccessLevel.PROTECTED)
    private T concurrentObject;
    @Getter(AccessLevel.PROTECTED)
    private T defaultObject;

    protected void ensureProfile(final boolean concurrent) {
    }

    @Override
    public final boolean switchProfile(final boolean concurrent) {
        ensureProfile(concurrent);

        try {
            return parallel;
        } finally {
            parallel = concurrent;
        }
    }

    @Override
    public final T getObject() {
        if (parallel) {
            return getConcurrentObject();
        }

        return getDefaultObject();
    }
}
