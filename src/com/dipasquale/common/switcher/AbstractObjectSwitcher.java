package com.dipasquale.common.switcher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractObjectSwitcher<T> implements ObjectSwitcher<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -2031619467740284195L;
    private boolean isOn;

    @Override
    public final boolean switchObject(final boolean on) {
        try {
            return isOn;
        } finally {
            isOn = on;
        }
    }

    protected abstract T getOn();

    protected abstract T getOff();

    @Override
    public final T getObject() {
        if (isOn) {
            return getOn();
        }

        return getOff();
    }
}
