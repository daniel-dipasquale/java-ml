package com.dipasquale.ai.rl.neat.speciation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReproductionType {
    MATE_AND_MUTATE,
    MATE_ONLY,
    MUTATE_ONLY,
    CLONE
}
