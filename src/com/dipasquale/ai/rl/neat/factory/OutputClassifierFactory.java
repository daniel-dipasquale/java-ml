package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.ProbabilityClassifier;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class OutputClassifierFactory<T> implements ObjectFactory<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -5811185015657812759L;
    private final FloatFactory floatFactory;
    private final ProbabilityClassifier<T> probabilityClassifier;

    @Override
    public T create() {
        float value = floatFactory.create();

        return probabilityClassifier.get(value);
    }
}
