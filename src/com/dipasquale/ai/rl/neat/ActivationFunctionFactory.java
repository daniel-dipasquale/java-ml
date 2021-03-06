package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;

@FunctionalInterface
interface ActivationFunctionFactory {
    ActivationFunction next();
}
