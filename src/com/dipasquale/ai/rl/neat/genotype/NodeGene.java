/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Generated
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public final class NodeGene implements Serializable {
    @Serial
    private static final long serialVersionUID = -4174686982693760386L;
    @ToString.Include
    private final SequentialId id;
    @ToString.Include
    private final NodeGeneType type;
    @ToString.Include
    private final float bias;
    private final ActivationFunction activationFunction;
}
