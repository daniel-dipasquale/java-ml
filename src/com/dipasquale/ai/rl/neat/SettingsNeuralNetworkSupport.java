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

    <T extends Comparable<T>> ContextDefaultComponentFactory<T, ContextDefaultNeuralNetworkSupport<T>> createFactory() {
        return c -> {
            ArgumentValidator.getInstance().ensureFalse(c.connections().allowRecurrentConnections(), "connections.allowRecurrentConnections", "is not support yet");

            return new ContextDefaultNeuralNetworkSupport<>(NeuralNetworkFeedForward::new);
        };
    }
}
