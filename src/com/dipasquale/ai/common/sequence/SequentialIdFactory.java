package com.dipasquale.ai.common.sequence;

import com.dipasquale.common.factory.ObjectFactory;

public interface SequentialIdFactory<T extends SequentialId<T>> extends ObjectFactory<T> {
    void reset();
}
