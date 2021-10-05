package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
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
    private final Collection<InputConnection> inputs;
    @Getter
    private final Collection<OutputConnection> outputs;

    public SequentialId getId() {
        return node.getId();
    }

    public NodeGeneType getType() {
        return node.getType();
    }

    public float getValue(final float value, final float weight) {
        return node.getActivationFunction().forward(value * weight + node.getBias());
    }

    public float getValue(final NeuronValueGroup neuronValues) {
        float value = neuronValues.getValue(getId());

        return getValue(value, 1f);
    }

    public float getValue(final NeuronValueGroup neuronValues, final OutputConnection output) {
        float value = neuronValues.getValue(getId(), output.getTargetNeuronId());

        return getValue(value, output.getConnectionWeight());
    }

    @Override
    public String toString() {
        return node.toString();
    }
}

