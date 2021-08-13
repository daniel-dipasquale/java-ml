/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.switcher;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serial;

@Getter(AccessLevel.PROTECTED)
public final class DefaultObjectSwitcher<T> extends AbstractObjectSwitcher<T> {
    @Serial
    private static final long serialVersionUID = -4711611220090246081L;

    public DefaultObjectSwitcher(final boolean isOn, final T onObject, final T offObject) {
        super(isOn, onObject, offObject);
    }

    public DefaultObjectSwitcher(final boolean isOn, final T object) {
        this(isOn, object, object);
    }
}