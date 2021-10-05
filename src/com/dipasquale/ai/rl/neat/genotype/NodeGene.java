package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Generated
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class NodeGene implements Serializable {
    @Serial
    private static final long serialVersionUID = -4174686982693760386L;
    private final SequentialId id;
    private final NodeGeneType type;
    private final float bias;
    private final ActivationFunction activationFunction;

    @Override
    public String toString() {
        return String.format("id: %s, type: %s, bias: %f, activationFunction: %s", id, type, bias, activationFunction);
    }
}
