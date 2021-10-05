package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextConnectionGeneSupport;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

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
    private final FloatNumber multiCycleAllowanceRate = FloatNumber.literal(0.0f);

    DefaultContextConnectionGeneSupport create(final GenesisGenomeTemplate genesisGenomeTemplate, final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport) {
        return DefaultContextConnectionGeneSupport.create(parallelismSupport, randomSupports, randomSupport, genesisGenomeTemplate, this);
    }
}
