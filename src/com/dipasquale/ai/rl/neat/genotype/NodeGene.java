package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Generated
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class NodeGene {
    @EqualsAndHashCode.Include
    @ToString.Include
    private final SequentialId id;
    @ToString.Include
    private final NodeGeneType type;
    @ToString.Include
    private final float bias;
    private final ActivationFunction activationFunction;
}
