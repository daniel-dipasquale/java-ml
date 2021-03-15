package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.ArgumentValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsNeuralNetworkSupport {
    @Builder.Default
    private final SettingsNeuralNetworkType type = SettingsNeuralNetworkType.Default;

    ContextDefaultComponentFactory<ContextDefaultNeuralNetworkSupport> createFactory(final SettingsConnectionGeneSupport connections) {
        return c -> {
            ArgumentValidator.ensureFalse(connections.isMultipleRecurrentCyclesAllowed(), "connections.allowRecurrentConnections", "is not support yet");

            return new ContextDefaultNeuralNetworkSupport(NeuralNetworkDefault::new);
        };
    }
}
