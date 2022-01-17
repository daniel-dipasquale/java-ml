package com.dipasquale.ai.common.function.activation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ActivationFunctionType {
    IDENTITY(IdentityActivationFunction.getInstance()),
    RE_LU(ReLUActivationFunction.getInstance()),
    SIGMOID(SigmoidActivationFunction.getInstance()),
    STEEPENED_SIGMOID(SteepenedSigmoidActivationFunction.getInstance()),
    TAN_H(TanHActivationFunction.getInstance()),
    STEP(StepActivationFunction.getInstance());

    private static final Map<ActivationFunction, ActivationFunctionType> ACTIVATION_FUNCTION_TYPES = createActivationFunctionTypes();
    private final ActivationFunction reference;

    private static Map<ActivationFunction, ActivationFunctionType> createActivationFunctionTypes() {
        Map<ActivationFunction, ActivationFunctionType> activationFunctionTypes = new IdentityHashMap<>();

        Arrays.stream(ActivationFunctionType.values()).forEach(aft -> activationFunctionTypes.put(aft.reference, aft));

        return activationFunctionTypes;
    }

    public static ActivationFunctionType from(final ActivationFunction activationFunction) {
        return ACTIVATION_FUNCTION_TYPES.get(activationFunction);
    }
}
