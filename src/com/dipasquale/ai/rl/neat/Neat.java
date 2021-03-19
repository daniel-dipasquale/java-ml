package com.dipasquale.ai.rl.neat;

public interface Neat {
    static NeatCollective createCollective(final SettingsCollective settings) {
        return new NeatCollectiveSynchronized(settings.createContext());
    }
}
