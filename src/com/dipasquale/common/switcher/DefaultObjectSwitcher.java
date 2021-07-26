package com.dipasquale.common.switcher;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serial;

@Getter(AccessLevel.PROTECTED)
public final class DefaultObjectSwitcher<T> extends AbstractObjectSwitcher<T> {
    @Serial
    private static final long serialVersionUID = -4711611220090246081L;
    private final T on;
    private final T off;

    public DefaultObjectSwitcher(final boolean isOn, final T onValue, final T offValue) {
        super(isOn);
        this.on = onValue;
        this.off = offValue;
    }

    public DefaultObjectSwitcher(final boolean isOn, final T value) {
        this(isOn, value, value);
    }
}