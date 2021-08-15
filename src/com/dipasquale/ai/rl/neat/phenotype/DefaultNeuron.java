package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
@Builder(access = AccessLevel.PACKAGE)
public final class DefaultNeuron implements Neuron {
    private final NodeGene node;
    @Getter
    private final Collection<InputNeuron> inputs;
    @Getter
    private final Collection<OutputNeuron> outputs;

    @Override
    public SequentialId getId() {
        return node.getId();
    }

    @Override
    public NodeGeneType getType() {
        return node.getType();
    }

    @Override
    public float getBias() {
        return node.getBias();
    }

    @Override
    public ActivationFunction getActivationFunction() {
        return node.getActivationFunction();
    }

    @Override
    public String toString() {
        return node.toString();
    }
}

