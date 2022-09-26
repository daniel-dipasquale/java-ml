package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.factory.IntegerFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class PopulationSizeProvider implements Serializable {
    @Serial
    private static final long serialVersionUID = 4907819657963763149L;
    private final IntegerFactory integerFactory;
    @Getter
    private int value;
    private int nextValue;
    private final IntegerFactory proxyIntegerFactory;

    public PopulationSizeProvider(final IntegerFactory integerFactory) {
        this.integerFactory = integerFactory;
        this.value = integerFactory.create();
        this.nextValue = integerFactory.create();
        this.proxyIntegerFactory = new ProxyIntegerFactory(this);
    }

    public void reinitialize(final int populationSize) {
        value = Math.max(nextValue, populationSize);
        nextValue = integerFactory.create();
    }

    public IntegerFactory toFactory() {
        return proxyIntegerFactory;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ProxyIntegerFactory implements IntegerFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 4723125884756547089L;
        private final PopulationSizeProvider provider;

        @Override
        public int create() {
            return provider.value;
        }
    }
}
