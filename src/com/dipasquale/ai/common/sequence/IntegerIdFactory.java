package com.dipasquale.ai.common.sequence;

import java.io.Serial;
import java.io.Serializable;

public final class IntegerIdFactory implements Serializable {
    @Serial
    private static final long serialVersionUID = 3884150086507363982L;
    private int id = 0;

    public int next() {
        int value = id++;

        if (value != Integer.MIN_VALUE) {
            return value;
        }

        throw new SequenceEndedException();
    }

    public void reset() {
        id = 0;
    }
}
