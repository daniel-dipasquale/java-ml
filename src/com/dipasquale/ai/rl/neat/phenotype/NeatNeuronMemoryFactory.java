package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeatNeuronMemoryFactory implements ObjectFactory<NeatNeuronMemory>, Serializable {
    @Serial
    private static final long serialVersionUID = 6149135438136278802L;
    private final Genome genome;

    @Override
    public NeatNeuronMemory create() {
        return new NeatNeuronMemory(genome);
    }
}
