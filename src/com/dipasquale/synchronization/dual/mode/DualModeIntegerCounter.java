package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.DefaultIntegerCounter;
import com.dipasquale.common.IntegerCounter;
import com.dipasquale.common.concurrent.AtomicIntegerCounter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DualModeIntegerCounter implements IntegerCounter, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -7472742036609010053L;
    private boolean parallel;
    @EqualsAndHashCode.Include
    private transient IntegerCounter counter;

    public DualModeIntegerCounter(final boolean concurrent, final int value) {
        this(concurrent, create(concurrent, value));
    }

    public DualModeIntegerCounter(final boolean concurrent) {
        this(concurrent, -1);
    }

    private static IntegerCounter create(final boolean concurrent, final int value) {
        if (concurrent) {
            return new AtomicIntegerCounter(value);
        }

        return new DefaultIntegerCounter(value);
    }

    @Override
    public int increment(final int delta) {
        return counter.increment(delta);
    }

    @Override
    public int current() {
        return counter.current();
    }

    @Override
    public int current(final int value) {
        return counter.current(value);
    }

    @Override
    public int compareTo(final Integer other) {
        return counter.compareTo(other);
    }

    @Override
    public void switchMode(final boolean concurrent) {
        parallel = concurrent;
        counter = create(concurrent, counter.current());
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        counter = create(parallel, (int) objectInputStream.readObject());
    }

    @Serial
    private void writeObject(final ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(counter.current());
    }
}
