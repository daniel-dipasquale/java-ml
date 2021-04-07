package com.dipasquale.ai.common;

import java.io.Serializable;

@FunctionalInterface
public interface GateProvider extends Serializable {
    boolean isOn();
}
