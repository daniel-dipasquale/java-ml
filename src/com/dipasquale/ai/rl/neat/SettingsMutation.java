package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultMutation;
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
    private final SettingsFloatNumber replaceConnectionWeightRate = SettingsFloatNumber.literal(0.75f);
    @Builder.Default
    private final SettingsFloatNumber disableConnectionExpressedRate = SettingsFloatNumber.literal(0.2f);

    ContextDefaultComponentFactory<ContextDefaultMutation> createFactory() {
        return context -> new ContextDefaultMutation(addNodeMutationsRate.get(), addConnectionMutationsRate.get(), perturbConnectionWeightRate.get(), replaceConnectionWeightRate.get(), disableConnectionExpressedRate.get());
    }
}
