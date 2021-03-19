package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultCrossOver;
import com.dipasquale.ai.rl.neat.genotype.GenomeCrossOver;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsCrossOver {
    @Builder.Default
    private final SettingsFloatNumber rate = SettingsFloatNumber.literal(0.8f);
    @Builder.Default
    private final SettingsFloatNumber overrideExpressedRate = SettingsFloatNumber.literal(0.5f);
    @Builder.Default
    private final SettingsFloatNumber useRandomParentWeightRate = SettingsFloatNumber.literal(1f);

    ContextDefaultComponentFactory<ContextDefaultCrossOver> createFactory() {
        return context -> new ContextDefaultCrossOver(rate.get(), overrideExpressedRate.get(), useRandomParentWeightRate.get(), new GenomeCrossOver(context));
    }
}
