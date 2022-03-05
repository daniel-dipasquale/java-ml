package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ConnectionGeneSupport {
    @Builder.Default
    private final FloatNumber weightFactory = FloatNumber.random(RandomType.BELL_CURVE, 2f);
    @Builder.Default
    private final FloatNumber weightPerturber = FloatNumber.literal(2.5f);
    @Builder.Default
    private final RecurrentStateType recurrentStateType = RecurrentStateType.DEFAULT;
    @Builder.Default
    private final FloatNumber recurrentAllowanceRate = FloatNumber.literal(0.2f);
    @Builder.Default
    private final FloatNumber unrestrictedDirectionAllowanceRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber multiCycleAllowanceRate = FloatNumber.literal(0f);

    ContextObjectConnectionGeneSupport create(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate) {
        return ContextObjectConnectionGeneSupport.create(initializationContext, genesisGenomeTemplate, this);
    }
}
