package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;

import java.util.Collection;

final class NeuronStrategy<T extends Neuron> implements Neuron {
    private final NeuronPromoter<T> neuronPromoter;
    private final T originalNeuron;
    private Neuron neuron;
    private boolean isRecurrent;

    NeuronStrategy(final NeuronPromoter<T> neuronPromoter, final T neuron) {
        this.neuronPromoter = neuronPromoter;
        this.originalNeuron = neuron;
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
    public Collection<NeuronInput> getInputs() {
        return neuron.getInputs();
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
    public void setValue(final float newValue) {
        neuron.setValue(newValue);
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
        neuron = neuronPromoter.promote(originalNeuron);
    }

    @Override
    public String toString() {
        return neuron.toString();
    }
}
