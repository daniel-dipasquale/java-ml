package com.dipasquale.ai.common;

import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface ActivationFunctionProvider extends Supplier<ActivationFunction>, Serializable {
}
