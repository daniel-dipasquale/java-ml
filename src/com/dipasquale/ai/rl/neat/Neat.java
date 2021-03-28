package com.dipasquale.ai.rl.neat;

public interface Neat {
    static NeatEvaluator createEvaluator(final SettingsEvaluator settings) {
        return new NeatEvaluatorSynchronized(settings.createContext());
    }
}
