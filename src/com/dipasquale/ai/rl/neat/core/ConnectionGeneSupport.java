package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextConnectionGeneSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ConnectionGeneSupport {
    @Builder.Default
    private final FloatNumber weightFactory = FloatNumber.random(RandomType.UNIFORM, -0.5f, 0.5f);
    @Builder.Default
    private final FloatNumber weightPerturber = FloatNumber.literal(2.5f);
    @Builder.Default
    private final FloatNumber recurrentAllowanceRate = FloatNumber.literal(0.2f);
    @Builder.Default
    private final RecurrentStateType recurrentStateType = RecurrentStateType.DEFAULT;
    @Builder.Default
    private final FloatNumber multiCycleAllowanceRate = FloatNumber.literal(0.0f);

    DefaultContextConnectionGeneSupport create(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate) {
        return DefaultContextConnectionGeneSupport.create(initializationContext, genesisGenomeTemplate, this);
    }
}
