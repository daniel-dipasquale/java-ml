/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.switcher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractObjectSwitcher<T> implements ObjectSwitcher<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -2031619467740284195L;
    private boolean isOn;
    @Getter(AccessLevel.PROTECTED)
    private T onObject;
    @Getter(AccessLevel.PROTECTED)
    private T offObject;

    @Override
    public final boolean switchObject(final boolean on) {
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
