package com.dipasquale.ai.common.sequence;

import java.io.Serial;

public final class SequenceEndedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8708057800250780698L;

    SequenceEndedException() {
        super("SequentialId reach its end");
    }
}
