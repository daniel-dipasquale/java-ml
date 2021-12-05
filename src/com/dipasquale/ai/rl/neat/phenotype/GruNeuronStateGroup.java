package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.common.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GruNeuronStateGroup extends RecurrentNeuronStateGroup {
    private static final String SHORT_TERM_DIMENSION = "HS";
    private static final SigmoidActivationFunction SIGMOID_ACTIVATION_FUNCTION = SigmoidActivationFunction.getInstance();
    private static final TanHActivationFunction TAN_H_ACTIVATION_FUNCTION = TanHActivationFunction.getInstance();
    private final NeuronMemory memory;

    @Override
    protected float getRecurrentValue(final Id id, final Id inputId) {
        float inputValue = getValue(id);
        float oldShortTermValue = getValue(memory, SHORT_TERM_DIMENSION, id);
        float resetOrUpdateGate = SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldShortTermValue);
        float candidateShortTermValue = TAN_H_ACTIVATION_FUNCTION.forward(inputValue + oldShortTermValue * resetOrUpdateGate);

        return oldShortTermValue * resetOrUpdateGate + (1f - resetOrUpdateGate) * candidateShortTermValue;
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(SHORT_TERM_DIMENSION, id, value, inputId);
    }
}
