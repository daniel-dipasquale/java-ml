package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum SettingsOutputActivationFunction {
    COPY_FROM_HIDDEN(null),
    RANDOM(SettingsActivationFunction.RANDOM),
    IDENTITY(SettingsActivationFunction.IDENTITY),
    RE_LU(SettingsActivationFunction.RE_LU),
    SIGMOID(SettingsActivationFunction.SIGMOID),
    TAN_H(SettingsActivationFunction.TAN_H);

    private final SettingsActivationFunction translated;
}
