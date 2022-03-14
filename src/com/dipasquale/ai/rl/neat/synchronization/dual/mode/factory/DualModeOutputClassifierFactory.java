package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeOutputClassifierFactory<TFloatFactory extends FloatFactory & DualModeObject, TItem> implements ObjectFactory<TItem>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -5811185015657812759L;
    private final TFloatFactory floatFactory;
    private final ProbabilityClassifier<TItem> probabilityClassifier;

    @Override
    public TItem create() {
        float value = floatFactory.create();

        return probabilityClassifier.get(value);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        floatFactory.activateMode(concurrencyLevel);
    }
}
