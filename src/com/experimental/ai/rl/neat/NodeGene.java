package com.experimental.ai.rl.neat;

import com.experimental.ai.ActivationFunction;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
final class NodeGene<T> {
    @EqualsAndHashCode.Include
    private final T id;
    private final Type type;
    private final float bias;
    private final ActivationFunction activationFunction;

    public enum Type {
        Input,
        Output,
        Hidden
    }
}
