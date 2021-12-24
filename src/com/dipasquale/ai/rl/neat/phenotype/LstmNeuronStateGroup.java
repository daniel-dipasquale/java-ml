package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class LstmNeuronStateGroup extends RecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "LSTM_H";
    private static final String CELL_DIMENSION = "LSTM_C";
    private static final SigmoidActivationFunction SIGMOID_ACTIVATION_FUNCTION = SigmoidActivationFunction.getInstance();
    private static final TanHActivationFunction TAN_H_ACTIVATION_FUNCTION = TanHActivationFunction.getInstance();
    private final NeuronMemory memory;
    private final Map<Id, Float> cycleStates = new HashMap<>();

    @Override
    protected float getRecurrentValue(final Id id) {
        float inputValue = getValue(id);
        float oldHiddenValue = getValue(memory, HIDDEN_DIMENSION, id);
        float oldCellValue = getValue(memory, CELL_DIMENSION, id);
        float forgetGate = SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue + oldCellValue);
        float inputGate = SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue + oldCellValue + 1f);
        float candidateCellValue = TAN_H_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue + 2f);
        float newCellValue = oldCellValue * forgetGate + candidateCellValue * inputGate;

        cycleStates.put(id, newCellValue);

        return SIGMOID_ACTIVATION_FUNCTION.forward(inputValue + oldHiddenValue + newCellValue + 3f) * TAN_H_ACTIVATION_FUNCTION.forward(newCellValue);
    }

    @Override
    protected void setMemoryValue(final Id id, final float value, final Id inputId) {
        memory.setValue(HIDDEN_DIMENSION, id, value, inputId);
    }

    @Override
    public void endCycle(final Id id) {
        Float value = cycleStates.remove(id);
        float valueFixed = Objects.requireNonNullElse(value, 0f);

        memory.setValue(CELL_DIMENSION, id, valueFixed, id);
    }
}
