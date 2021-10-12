package com.dipasquale.data.structure.probabilistic;

import java.io.Serial;
import java.security.NoSuchAlgorithmException;

public final class NoSuchAlgorithmRuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -1802245231536508289L;

    NoSuchAlgorithmRuntimeException(final NoSuchAlgorithmException cause) {
        super(cause.getMessage(), cause);
    }
}
