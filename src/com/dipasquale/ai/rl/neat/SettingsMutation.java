package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsMutation {
    @Builder.Default
    private final SettingsFloatNumber addNodeMutationsRate = SettingsFloatNumber.literal(0.1f);
    @Builder.Default
    private final SettingsFloatNumber addConnectionMutationsRate = SettingsFloatNumber.literal(0.1f);
    @Builder.Default
    private final SettingsFloatNumber perturbConnectionWeightRate = SettingsFloatNumber.literal(0.9f);
    @Builder.Default
    private final SettingsFloatNumber changeConnectionExpressedRate = SettingsFloatNumber.literal(0.2f);

    <T extends Comparable<T>> ContextDefaultComponentFactory<T, ContextDefaultMutation> createFactory() {
        return c -> new ContextDefaultMutation(addNodeMutationsRate.get(), addConnectionMutationsRate.get(), perturbConnectionWeightRate.get(), changeConnectionExpressedRate.get());
    }
}
