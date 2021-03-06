package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class NeuronOutput<T> {
    private final T neuronId;
    private final float connectionWeight;
}
