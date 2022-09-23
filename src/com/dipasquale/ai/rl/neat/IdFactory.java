package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.sequence.LongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.NamedLongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.common.LongValue;
import com.dipasquale.common.StandardLongValue;

import java.io.Serial;
import java.io.Serializable;

public final class IdFactory implements SequentialIdFactory<Id>, Serializable {
    @Serial
    private static final long serialVersionUID = -8362275222819817723L;
    private final NamedLongSequentialIdFactory sequentialIdFactory;

    private IdFactory(final String name, final LongSequentialIdFactory idFactory) {
        this.sequentialIdFactory = new NamedLongSequentialIdFactory(name, idFactory);
    }

    private IdFactory(final String name, final LongValue id) {
        this(name, new LongSequentialIdFactory(id));
    }

    public IdFactory(final IdType idType) {
        this(idType.getName(), new StandardLongValue());
    }

    @Override
    public Id create() {
        return new Id(sequentialIdFactory.create());
    }

    @Override
    public void reset() {
        sequentialIdFactory.reset();
    }
}
