package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.function.activation.ReLUActivationFunction;
import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.SteepenedSigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.StepActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class NeuronLayerReader {
    private static final Map<ActivationFunction, ActivationFunctionType> ACTIVATION_FUNCTION_TYPES = createActivationFunctionTypes();
    private final List<Neuron> neurons;
    private final NeuronStateGroup neuronState;

    private static Map<ActivationFunction, ActivationFunctionType> createActivationFunctionTypes() {
        Map<ActivationFunction, ActivationFunctionType> activationFunctionTypes = new IdentityHashMap<>();

        activationFunctionTypes.put(IdentityActivationFunction.getInstance(), ActivationFunctionType.IDENTITY);
        activationFunctionTypes.put(ReLUActivationFunction.getInstance(), ActivationFunctionType.RE_LU);
        activationFunctionTypes.put(SigmoidActivationFunction.getInstance(), ActivationFunctionType.SIGMOID);
        activationFunctionTypes.put(SteepenedSigmoidActivationFunction.getInstance(), ActivationFunctionType.STEEPENED_SIGMOID);
        activationFunctionTypes.put(TanHActivationFunction.getInstance(), ActivationFunctionType.TAN_H);
        activationFunctionTypes.put(StepActivationFunction.getInstance(), ActivationFunctionType.STEP);

        return activationFunctionTypes;
    }

    public int size() {
        return neurons.size();
    }

    public ActivationFunctionType getType(final int index) {
        Neuron neuron = neurons.get(index);

        return ACTIVATION_FUNCTION_TYPES.get(neuron.getActivationFunction());
    }

    public float getValue(final int index) {
        Neuron neuron = neurons.get(index);

        return neuronState.calculateValue(neuron);
    }
}
