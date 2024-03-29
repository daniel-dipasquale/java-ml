package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Generated
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class NodeGene implements Serializable {
    @Serial
    private static final long serialVersionUID = -4174686982693760386L;
    private final Id id;
    private final NodeGeneType type;
    private final float bias;
    private final List<Float> recurrentBiases;
    private final ActivationFunction activationFunction;

    @Override
    public String toString() {
        return String.format("id: %s, type: %s, bias: %f, activationFunction: %s", id, type, bias, activationFunction);
    }
}
