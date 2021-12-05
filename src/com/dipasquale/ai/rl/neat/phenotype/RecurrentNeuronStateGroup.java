package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.common.Id;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.ConnectionType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class RecurrentNeuronStateGroup implements NeuronStateGroup {
    private final Map<Id, NeuronState> states = new HashMap<>();

    private static float getValue(final Map<Id, NeuronState> states, final Id nodeId) {
        NeuronState state = states.get(nodeId);

        if (state == null) {
            return 0f;
        }

        return state.getValue();
    }

    @Override
    public final float getValue(final Id id) {
        return getValue(states, id);
    }

    protected static float getValue(final NeuronMemory memory, final String dimension, final Id nodeId) {
        Float value = memory.getValue(dimension, nodeId);

        if (value == null) {
            return 0f;
        }

        return value;
    }

    protected abstract float getRecurrentValue(Id id, Id inputId);

    @Override
    public final float getValue(final Id id, final Id inputId) {
        if (ConnectionGene.getType(inputId, id) == ConnectionType.FORWARD) {
            return getValue(id);
        }

        return getRecurrentValue(id, inputId);
    }

    @Override
    public final void setValue(final Id id, final float value) {
        NeuronState state = states.computeIfAbsent(id, k -> new NeuronState());

        state.clear();
        state.replace(id, value);
    }

    protected abstract void setMemoryValue(Id id, float value, Id inputId);

    @Override
    public final void addValue(final Id id, final float value, final Id inputId) {
        NeuronState state = states.computeIfAbsent(id, k -> new NeuronState());

        state.replace(inputId, value);
        setMemoryValue(id, value, inputId);
    }

    @Override
    public final void clear() {
        states.clear();
    }
}
