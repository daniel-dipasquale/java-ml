package com.experimental.concurrent;

import com.dipasquale.threading.wait.handle.SlidingWaitHandle;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public final class AtomicLongScaled implements Serializable {
    private static final long serialVersionUID = 5463864851297275669L;
    private static final AtomicReferenceFieldUpdater<AtomicLongScaled, Record> RECORD_FIELD_UPDATER = AtomicReferenceFieldUpdater.newUpdater(AtomicLongScaled.class, Record.class, "record");
    private final SlidingWaitHandle waitHandle;
    private volatile Record record;

    public AtomicLongScaled() {
        SlidingWaitHandle waitHandle = new SlidingWaitHandle();

        this.waitHandle = waitHandle;
        this.record = new Record(waitHandle, null, 4, 0L);
    }

    private static int currentThreadHashCode() {
        int hashCode = System.identityHashCode(Thread.currentThread());

        hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
        hashCode ^= (hashCode >>> 7) ^ (hashCode >>> 4);

        return hashCode << 2;
    }

    public long get() {
        return record.sum(0L);
    }

    public long getEstimate() {
        return record.estimateSum(0);
    }

    private boolean compareAndSet(final Record oldRecord, final Record newRecord) {
        return RECORD_FIELD_UPDATER.compareAndSet(this, oldRecord, newRecord);
    }

    public void set(final long value) {
        Record recordNew = new Record(waitHandle, null, 4, value);

        for (boolean done = compareAndSet(record, recordNew); !done; ) {
            done = compareAndSet(record, recordNew);
        }
    }

    private long add(final long value, final long mask) {
        return record.add(value, mask, currentThreadHashCode(), this);
    }

    public void add(final long value) {
        add(value, 0L);
    }

    public void decrement() {
        add(-1L, 0L);
    }

    public void increment() {
        add(1L, 0L);
    }

    public int size() {
        return record.values.length;
    }

    public String toString() {
        return record.toString(0L);
    }

    private static class Record implements Serializable {
        private static final long serialVersionUID = 8522043966059071665L;
        private static final Unsafe UNSAFE_ARRAY = UnsafeProvider.getInstance().getUnsafe();
        private static final long UNSAFE_ARRAY_BASE_OFFSET = UNSAFE_ARRAY.arrayBaseOffset(long[].class);
        private static final long UNSAFE_ARRAY_INDEX_SCALE = UNSAFE_ARRAY.arrayIndexScale(long[].class);
        private static final AtomicLongFieldUpdater<Record> THREADS_RESIZING_FIELD_UPDATER = AtomicLongFieldUpdater.newUpdater(Record.class, "threadsResizing");
        private static final int MAX_SPIN = 2;
        private final SlidingWaitHandle waitHandle;
        private final Record next;
        private final long[] values;
        private volatile long sumCache;
        private volatile long sumCacheFuzzy;
        private volatile long sumCacheFuzzyTime;
        private volatile long threadsResizing;

        public Record(final SlidingWaitHandle waitHandle, final Record next, final int size, final long initialValue) {
            this.waitHandle = waitHandle;
            this.next = next;
            this.values = new long[size];
            this.values[0] = initialValue;
            this.sumCache = Long.MIN_VALUE;
            this.sumCacheFuzzy = 0L;
            this.sumCacheFuzzyTime = Long.MIN_VALUE;
            this.threadsResizing = 0L;
        }

        private static long rawIndex(final long[] array, final int index) {
            return UNSAFE_ARRAY_BASE_OFFSET + (long) index * UNSAFE_ARRAY_INDEX_SCALE;
        }

        private static boolean compareAndSet(final long[] array, final int index, final long expected, final long updated) {
            return UNSAFE_ARRAY.compareAndSwapLong(array, rawIndex(array, index), expected, updated);
        }

        public long add(final long value, final long mask, final int hashCode, final AtomicLongScaled owner) {
            int index = hashCode & (values.length - 1);
            long valueOld = values[index];
            boolean done = compareAndSet(values, index, valueOld & ~mask, valueOld + value);

            if (sumCache != Long.MIN_VALUE) {
                sumCache = Long.MIN_VALUE;
            }

            if (done || (valueOld & mask) != 0) {
                return valueOld;
            }

            int count = 0;

            for (done = false; !done; ) {
                valueOld = values[index];

                if ((valueOld & mask) != 0) {
                    return valueOld;
                }

                count++;
                done = compareAndSet(values, index, valueOld, valueOld + value);
            }

            if (count < MAX_SPIN || values.length >= 1_024 * 1_024) {
                return valueOld;
            }

            long threadsResizingCopy = threadsResizing;
            int bytes = (values.length << 1) << 3;

            while (!THREADS_RESIZING_FIELD_UPDATER.compareAndSet(this, threadsResizingCopy, threadsResizingCopy + bytes)) {
                threadsResizingCopy = threadsResizing;
            }

            threadsResizingCopy += bytes;

            if (owner.record != this) {
                return valueOld;
            }

            if ((threadsResizingCopy >> 17) != 0L) {
                synchronized (this) {
                    try {
                        waitHandle.await(threadsResizingCopy >> 17, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();

                        throw new RuntimeException("thread was interrupted", e);
                    }
                }

                if (owner.record != this) {
                    return valueOld;
                }
            }

            Record recordNew = new Record(waitHandle, this, values.length * 2, 0L);

            owner.compareAndSet(this, recordNew);

            return valueOld;
        }

        public long sum(final long mask) {
            long sum = sumCache;

            if (sum != Long.MIN_VALUE) {
                return sum;
            }

            sum = next == null ? 0L : next.sum(mask);

            for (long value : values) {
                sum += value & ~mask;
            }

            sumCache = sum;

            return sum;
        }

        public long estimateSum(final long mask) {
            if (values.length <= 64) {
                return sum(mask);
            }

            long millis = System.currentTimeMillis();

            if (sumCacheFuzzyTime != millis) {
                sumCacheFuzzy = sum(mask);
                sumCacheFuzzyTime = millis;
            }

            return sumCacheFuzzy;
        }

        public void allOr(final long mask) {
            for (int i = 0; i < values.length; i++) {
                boolean done = false;

                while (!done) {
                    long valueOld = values[i];

                    done = compareAndSet(values, i, valueOld, valueOld | mask);
                }
            }

            if (next != null) {
                next.allOr(mask);
            }

            if (sumCache != Long.MIN_VALUE) {
                sumCache = Long.MIN_VALUE;
            }
        }

        public void allAnd(final long mask) {
            for (int i = 0; i < values.length; i++) {
                boolean done = false;

                while (!done) {
                    long valueOld = values[i];

                    done = compareAndSet(values, i, valueOld, valueOld & mask);
                }
            }

            if (next != null) {
                next.allAnd(mask);
            }

            if (sumCache != Long.MIN_VALUE) {
                sumCache = Long.MIN_VALUE;
            }
        }

        public void allSet(final long val) {
            Arrays.fill(values, val);

            if (next != null) {
                next.allSet(val);
            }

            if (sumCache != Long.MIN_VALUE) {
                sumCache = Long.MIN_VALUE;
            }
        }

        private String toString(final long mask) {
            return Long.toString(sum(mask));
        }
    }
}
