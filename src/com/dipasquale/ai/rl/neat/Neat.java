package com.dipasquale.ai.rl.neat;

public interface Neat {
    static <T extends Comparable<T>> NeatCollective createCollective(final Settings<T> settings) {
        return new Population<>(settings.createContext());
    }
}
