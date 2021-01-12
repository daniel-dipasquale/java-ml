package com.dipasquale.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentHandler {
    @Getter
    private static final ConcurrentHandler instance = new ConcurrentHandler();

    public <T> T get(final ReadWriteLock readWriteLock, final Supplier<T> supplier) {
        readWriteLock.readLock().lock();

        try {
            return supplier.get();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public <T> void set(final ReadWriteLock readWriteLock, final Consumer<T> consumer, final T value) {
        readWriteLock.writeLock().lock();

        try {
            consumer.accept(value);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public <T> T getAndSet(final ReadWriteLock readWriteLock, final Supplier<T> supplier, final Consumer<T> consumer, final T value) {
        readWriteLock.writeLock().lock();

        try {
            T valueOld = supplier.get();

            consumer.accept(value);

            return valueOld;
        } finally {
            readWriteLock.writeLock().lock();
        }
    }

    public <T> T invoke(final List<Lock> locks, final Supplier<T> supplier) {
        for (Lock lock : locks) {
            lock.lock();
        }

        try {
            return supplier.get();
        } finally {
            for (int i = locks.size() - 1; i >= 0; i--) {
                locks.get(i).unlock();
            }
        }
    }

    public void invoke(final List<Lock> locks, final Runnable runnable) {
        invoke(locks, () -> {
            runnable.run();

            return null;
        });
    }

    public <TInput, TOutput> TOutput invoke(final List<Lock> locks, final Function<TInput, TOutput> function, final TInput input) {
        return invoke(locks, () -> function.apply(input));
    }
}
