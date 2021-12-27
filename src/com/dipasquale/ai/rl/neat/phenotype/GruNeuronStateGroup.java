package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GruNeuronStateGroup extends AbstractRecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "GRU_H";
    private static final SigmoidActivationFunction SIGMOID_ACTIVATION_FUNCTION = SigmoidActivationFunction.getInstance();
    private static final TanHActivationFunction TAN_H_ACTIVATION_FUNCTION = TanHActivationFunction.getInstance();
    private final NeuronMemory memory;

    @Override
    protected float getRecurrentValue(final Id id) {
        float inputValue = getValue(id);
        float oldHiddenValue = getValue(memory, HIDDEN_DIMENSION, id);
        float resetGate = SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue);
        float updateGate = SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue + 1f);
        float candidateHiddenValue = TAN_H_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue * resetGate);

        return oldHiddenValue * updateGate + (1f - updateGate) * candidateHiddenValue;
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(HIDDEN_DIMENSION, id, value, inputId);
    }
}
