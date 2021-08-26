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

@RequiredArgsConstructor
@Builder(access = AccessLevel.PACKAGE)
public final class Neuron implements Serializable {
    @Serial
    private static final long serialVersionUID = -7305330143842774982L;
    private final NodeGene node;
    @Getter
    private final Collection<InputNeuron> inputs;
    @Getter
    private final Collection<OutputNeuron> outputs;

    public SequentialId getId() {
        return node.getId();
    }

    public NodeGeneType getType() {
        return node.getType();
    }

    public float getValue(final float value) {
        return node.getActivationFunction().forward(value + node.getBias());
    }

    public float getValue(final NeuronValueGroup neuronValues) {
        float value = neuronValues.getValue(getId());

        return getValue(value);
    }

    @Override
    public String toString() {
        return node.toString();
    }
}

