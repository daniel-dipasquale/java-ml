package com.dipasquale.synchronization.dual.mode;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Getter
public final class ConcurrencyLevelState implements Serializable {
    @Serial
    private static final long serialVersionUID = 4628335495939492300L;
    private int current;
    private int maximum;

    public ConcurrencyLevelState(final int current) {
        this(current, Math.max(current, 1));
    }

    public int setCurrent(final int value) {
        try {
            return current;
        } finally {
            current = value;
            maximum = Math.max(maximum, value);
        }
    }
}
