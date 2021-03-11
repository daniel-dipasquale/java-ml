package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;

import java.util.Collection;
import java.util.Set;

final class NeuronStrategy<T extends Neuron> implements Neuron {
    private final NeuronPromoter<T> neuronPromoter;
    private Neuron neuron;
    private boolean isRecurrent;

    NeuronStrategy(final NeuronPromoter<T> neuronPromoter, final Neuron neuron) {
        this.neuronPromoter = neuronPromoter;
        this.neuron = neuron;
        this.isRecurrent = false;
    }

    @Override
    public SequentialId getId() {
        return neuron.getId();
    }

    @Override
    public NodeGeneType getType() {
        return neuron.getType();
    }

    @Override
    public ActivationFunction getActivationFunction() {
        return neuron.getActivationFunction();
    }

    @Override
    public Set<SequentialId> getInputIds() {
        return neuron.getInputIds();
    }

    @Override
    public Collection<NeuronOutput> getOutputs() {
        return neuron.getOutputs();
    }

    @Override
    public float getValue(final ActivationFunction activationFunction) {
        return neuron.getValue(activationFunction);
    }

    @Override
    public void forceValue(final float newValue) {
        neuron.forceValue(newValue);
    }

    @Override
    public void addToValue(final SequentialId id, final float delta) {
        neuron.addToValue(id, delta);
    }

    public void promoteToRecurrent() {
        if (isRecurrent) {
            return;
        }

        isRecurrent = true;
        neuron = neuronPromoter.promote((T) neuron);
    }
}
