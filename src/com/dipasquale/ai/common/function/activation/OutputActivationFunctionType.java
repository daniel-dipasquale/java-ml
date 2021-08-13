/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.function.activation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum OutputActivationFunctionType {
    COPY_FROM_HIDDEN(null),
    RANDOM(ActivationFunctionType.RANDOM),
    IDENTITY(ActivationFunctionType.IDENTITY),
    RE_LU(ActivationFunctionType.RE_LU),
    SIGMOID(ActivationFunctionType.SIGMOID),
    TAN_H(ActivationFunctionType.TAN_H),
    STEP(ActivationFunctionType.STEP);

    private final ActivationFunctionType translated;
}
