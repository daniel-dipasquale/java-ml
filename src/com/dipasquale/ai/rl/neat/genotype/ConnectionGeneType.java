package com.dipasquale.ai.rl.neat.genotype;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConnectionGeneType {
    REFLEXIVE,
    BACKWARD,
    FORWARD
}
