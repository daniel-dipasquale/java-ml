package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.DefaultLongCounter;
import com.dipasquale.common.LongCounter;
import com.dipasquale.common.concurrent.AtomicLongCounter;
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
public final class DualModeLongCounter implements LongCounter, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6310708806974536759L;
    private boolean parallel;
    @EqualsAndHashCode.Include
    private transient LongCounter counter;

    public DualModeLongCounter(final boolean concurrent, final long value) {
        this(concurrent, create(concurrent, value));
    }

    public DualModeLongCounter(final boolean concurrent) {
        this(concurrent, -1L);
    }

    private static LongCounter create(final boolean concurrent, final long value) {
        if (concurrent) {
            return new AtomicLongCounter(value);
        }

        return new DefaultLongCounter(value);
    }

    @Override
    public long increment(final long delta) {
        return counter.increment(delta);
    }

    @Override
    public long current() {
        return counter.current();
    }

    @Override
    public long current(final long value) {
        return counter.current(value);
    }

    @Override
    public int compareTo(final Long other) {
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
        counter = create(parallel, (long) objectInputStream.readObject());
    }

    @Serial
    private void writeObject(final ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(counter.current());
    }
}
