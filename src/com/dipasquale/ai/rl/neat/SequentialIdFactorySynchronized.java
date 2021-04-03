package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SequentialIdFactorySynchronized implements SequentialIdFactory {
    private final String name;
    private final SequentialIdFactory sequentialIdFactory;

    private SequentialId createSequentialId() {
        synchronized (sequentialIdFactory) {
            return sequentialIdFactory.next();
        }
    }

    @Override
    public SequentialId next() {
        SequentialId sequentialId = createSequentialId();

        return new SequentialIdStrategy(name, sequentialId);
    }

    @Override
    public void reset() {
        synchronized (sequentialIdFactory) {
            sequentialIdFactory.reset();
        }
    }
}
