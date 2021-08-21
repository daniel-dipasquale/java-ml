package com.dipasquale.common.profile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractObjectProfile<T> implements ObjectProfile<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -2031619467740284195L;
    private boolean isOn;
    @Getter(AccessLevel.PROTECTED)
    private T onObject;
    @Getter(AccessLevel.PROTECTED)
    private T offObject;

    @Override
    public final boolean switchProfile(final boolean on) {
        try {
            return isOn;
        } finally {
            isOn = on;
        }
    }

    @Override
    public final T getObject() {
        if (isOn) {
            return getOnObject();
        }

        return getOffObject();
    }
}
