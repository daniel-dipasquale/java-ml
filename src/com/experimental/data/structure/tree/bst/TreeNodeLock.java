package com.experimental.data.structure.tree.bst;

public interface TreeNodeLock<T extends TreeNodeLock<T>> extends Comparable<T> {
    void lock();

    void unlock();
}
