package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.common.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class LstmNeuronStateGroup extends RecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "LSTM_H";
    private static final String CELL_DIMENSION = "LSTM_C";
    private static final SigmoidActivationFunction SIGMOID_ACTIVATION_FUNCTION = SigmoidActivationFunction.getInstance();
    private static final TanHActivationFunction TAN_H_ACTIVATION_FUNCTION = TanHActivationFunction.getInstance();
    private final NeuronMemory memory;

    @Override
    protected float getRecurrentValue(final Id id, final Id inputId) {
        float inputValue = getValue(id);
        float oldHiddenValue = getValue(memory, HIDDEN_DIMENSION, id);
        float oldCellValue = getValue(memory, CELL_DIMENSION, id);
        float forgetOrInputGate = SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue);
        float candidateCellValue = TAN_H_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue) * forgetOrInputGate;
        float newCellValue = oldCellValue * forgetOrInputGate + candidateCellValue;

        try {
            return TAN_H_ACTIVATION_FUNCTION.forward(newCellValue) * forgetOrInputGate;
        } finally {
            memory.setValue(CELL_DIMENSION, id, newCellValue, inputId);
        }
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(HIDDEN_DIMENSION, id, value, inputId);
    }
}
