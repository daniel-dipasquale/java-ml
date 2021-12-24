package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
final class Neuron implements Serializable {
    @Serial
    private static final long serialVersionUID = -7305330143842774982L;
    private final NodeGene node;
    @Getter
    private final Collection<NeuronInputConnection> inputConnections;
    @Getter
    private final Collection<NeuronOutputConnection> outputConnections;

    public Id getId() {
        return node.getId();
    }

    public NodeGeneType getType() {
        return node.getType();
    }

    public float getValue(final float value, final float weight) {
        return node.getActivationFunction().forward(value * weight + node.getBias());
    }

    public float getValue(final NeuronStateGroup neuronState) {
        float value = neuronState.getValue(getId());

        return getValue(value, 1f);
    }

    public float getValue(final NeuronStateGroup neuronState, final NeuronOutputConnection outputConnection) {
        float value = neuronState.getValue(getId(), outputConnection.getOutputNeuronId());

        return getValue(value, outputConnection.getConnectionWeight());
    }

    @Override
    public String toString() {
        return node.toString();
    }
}

