package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SequentialIdFactoryDefault implements SequentialIdFactory {
    private final String name;
    private final SequentialIdFactory sequentialIdFactory;

    @Override
    public SequentialId next() {
        SequentialId sequentialId = sequentialIdFactory.next();

        return new SequentialIdStrategy(name, sequentialId);
    }

    @Override
    public void reset() {
        sequentialIdFactory.reset();
    }
}
