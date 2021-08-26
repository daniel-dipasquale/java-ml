package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.CyclicIntegerCounter;
import com.dipasquale.common.IntegerCounter;
import com.dipasquale.common.concurrent.AtomicCyclicIntegerCounter;
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
public final class DualModeCyclicIntegerCounter implements IntegerCounter, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8709366941955699964L;
    private boolean parallel;
    private final int max;
    private final int offset;
    @EqualsAndHashCode.Include
    private transient IntegerCounter counter;

    public DualModeCyclicIntegerCounter(final boolean concurrent, final int max, final int offset, final int value) {
        this(concurrent, max, offset, create(concurrent, max, offset, value));
    }

    public DualModeCyclicIntegerCounter(final boolean concurrent, final int max, final int offset) {
        this(concurrent, max, offset, 0);
    }

    public DualModeCyclicIntegerCounter(final boolean concurrent, final int max) {
        this(concurrent, max, -1);
    }

    private static IntegerCounter create(final boolean concurrent, final int max, final int offset, final int value) {
        if (concurrent) {
            return new AtomicCyclicIntegerCounter(max, offset, value);
        }

        return new CyclicIntegerCounter(max, offset, value);
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
        counter = create(concurrent, max, offset, counter.current());
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        counter = create(parallel, max, offset, (int) objectInputStream.readObject());
    }

    @Serial
    private void writeObject(final ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(counter.current());
    }
}
