/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

interface ExclusiveQueue<T> {
    void lock();

    void unlock();

    boolean isEmpty();

    T peek();

    T poll();

    void push(T item);

    void clear();
}
