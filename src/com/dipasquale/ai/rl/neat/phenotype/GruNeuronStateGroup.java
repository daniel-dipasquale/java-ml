package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.common.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GruNeuronStateGroup extends RecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "GRU_H";
    private static final SigmoidActivationFunction SIGMOID_ACTIVATION_FUNCTION = SigmoidActivationFunction.getInstance();
    private static final TanHActivationFunction TAN_H_ACTIVATION_FUNCTION = TanHActivationFunction.getInstance();
    private final NeuronMemory memory;

    @Override
    protected float getRecurrentValue(final Id id, final Id inputId) {
        float inputValue = getValue(id);
        float oldHiddenValue = getValue(memory, HIDDEN_DIMENSION, id);
        float resetOrUpdateGate = SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue);
        float candidateHiddenValue = TAN_H_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue * resetOrUpdateGate);

        return oldHiddenValue * resetOrUpdateGate + (1f - resetOrUpdateGate) * candidateHiddenValue;
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(HIDDEN_DIMENSION, id, value, inputId);
    }
}
