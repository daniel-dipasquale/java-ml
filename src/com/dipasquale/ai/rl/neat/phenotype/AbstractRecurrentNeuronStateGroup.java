package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.ConnectionType;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractRecurrentNeuronStateGroup implements NeuronStateGroup {
    private final Map<Id, NeuronState> states = new HashMap<>();

    protected float getValue(final Id neuronId) {
        NeuronState state = states.get(neuronId);

        if (state == null) {
            return 0f;
        }

        return state.getValue();
    }

    @Override
    public float calculateValue(final Neuron neuron) {
        float value = getValue(neuron.getId());

        return neuron.calculateValue(value);
    }

    protected abstract float calculateRecurrentValue(Neuron neuron, NeuronOutputConnection connection);

    @Override
    public float calculateValue(final Neuron neuron, final NeuronOutputConnection connection) {
        Id neuronId = neuron.getId();

        if (ConnectionGene.getType(neuronId, connection.getTargetNeuronId()) != ConnectionType.FORWARD) {
            return calculateRecurrentValue(neuron, connection);
        }

        float value = getValue(neuronId);

        return neuron.calculateValue(connection, value);

    }

    @Override
    public void setValue(final Id neuronId, final float value) {
        NeuronState state = states.computeIfAbsent(neuronId, k -> new NeuronState());

        state.clear();
        state.put(neuronId, value);
    }

    @Override
    public void addValue(final Id neuronId, final float value, final Id sourceNeuronId) {
        NeuronState state = states.computeIfAbsent(neuronId, k -> new NeuronState());

        state.put(sourceNeuronId, value);
    }

    @Override
    public abstract void endCycle(Id neuronId);

    @Override
    public final void clear() {
        states.clear();
    }
}
