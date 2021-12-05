package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.common.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class LstmNeuronStateGroup extends RecurrentNeuronStateGroup {
    private static final String SHORT_TERM_DIMENSION = "HS";
    private static final String LONG_TERM_DIMENSION = "CS";
    private static final SigmoidActivationFunction SIGMOID_ACTIVATION_FUNCTION = SigmoidActivationFunction.getInstance();
    private static final TanHActivationFunction TAN_H_ACTIVATION_FUNCTION = TanHActivationFunction.getInstance();
    private final NeuronMemory memory;

    @Override
    protected float getRecurrentValue(final Id id, final Id inputId) {
        float inputValue = getValue(id);
        float oldShortTermValue = getValue(memory, SHORT_TERM_DIMENSION, id);
        float oldLongTermValue = getValue(memory, LONG_TERM_DIMENSION, id);
        float forgetOrInputGate = SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldShortTermValue);
        float candidateLongTermValue = TAN_H_ACTIVATION_FUNCTION.forward(inputValue + oldShortTermValue) * forgetOrInputGate;
        float newLongTermValue = oldLongTermValue * forgetOrInputGate + candidateLongTermValue;

        try {
            return TAN_H_ACTIVATION_FUNCTION.forward(newLongTermValue) * forgetOrInputGate;
        } finally {
            memory.setValue(LONG_TERM_DIMENSION, id, newLongTermValue, inputId);
        }
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(SHORT_TERM_DIMENSION, id, value, inputId);
    }
}
