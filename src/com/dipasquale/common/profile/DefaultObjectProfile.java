package com.dipasquale.common.profile;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serial;

@Getter(AccessLevel.PROTECTED)
public final class DefaultObjectProfile<T> extends AbstractObjectProfile<T> {
    @Serial
    private static final long serialVersionUID = -4711611220090246081L;

    public DefaultObjectProfile(final boolean isOn, final T onObject, final T offObject) {
        super(isOn, onObject, offObject);
    }

    public DefaultObjectProfile(final boolean isOn, final T object) {
        this(isOn, object, object);
    }
}