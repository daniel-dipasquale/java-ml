package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsCrossOver {
    @Builder.Default
    private final SettingsFloatNumber rate = SettingsFloatNumber.literal(0.8f);
    @Builder.Default
    private final SettingsFloatNumber enforceExpressedRate = SettingsFloatNumber.literal(0.5f);
    @Builder.Default
    private final SettingsFloatNumber useRandomParentWeightRate = SettingsFloatNumber.literal(1f);

    <T extends Comparable<T>> ContextDefaultComponentFactory<T, ContextDefaultCrossOver<T>> createFactory() {
        return c -> new ContextDefaultCrossOver<>(rate.get(), enforceExpressedRate.get(), useRandomParentWeightRate.get(), new GenomeCrossOver<>(c));
    }
}
