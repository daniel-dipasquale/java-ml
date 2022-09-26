package com.dipasquale.common.gate;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class ConstantGate implements Gate, Serializable {
    @Serial
    private static final long serialVersionUID = -4019029192057751329L;
    private final boolean value;

    @Override
    public boolean isOn() {
        return value;
    }
}
