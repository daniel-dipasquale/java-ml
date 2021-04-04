package com.experimental.data.structure.tree.bst;

import com.dipasquale.threading.lock.ComparableLock;
import com.experimental.concurrent.ConcurrentHandler;
import lombok.EqualsAndHashCode;

import java.util.SortedSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@EqualsAndHashCode(callSuper = true)
class TreeNodeRedBlackConcurrent<TKey, TValue> extends TreeNodeConcurrentBase<TKey, TValue, TreeNodeRedBlack<TKey, TValue>, TreeNodeLockRedBlackConcurrent, TreeNodeRedBlackConcurrent<TKey, TValue>> implements TreeNodeRedBlack<TKey, TValue> {
    private static final ConcurrentHandler CONCURRENT_HANDLER = ConcurrentHandler.getInstance();
    @EqualsAndHashCode.Exclude
    private final ReadWriteLock isRedLock;

    public TreeNodeRedBlackConcurrent(final TreeNodeRedBlack<TKey, TValue> node, final ComparableLock comparableLock, final boolean shouldWritingBePriority) {
        super(node, comparableLock, shouldWritingBePriority);
        this.isRedLock = new ReentrantReadWriteLock(!shouldWritingBePriority);
    }

    @Override
    public boolean isRed() {
        return CONCURRENT_HANDLER.get(isRedLock, node::isRed);
    }

    @Override
    public boolean setRed(final boolean red) {
        return CONCURRENT_HANDLER.getAndSet(isRedLock, node::isRed, node::setRed, red);
    }

    private TreeNodeLockRedBlackConcurrent createLock() {
        return new TreeNodeLockRedBlackConcurrent(comparableLock, parentLock, leftLock, rightLock, isRedLock);
    }

    @Override
    public TreeNodeRedBlack<TKey, TValue> lockAcquirer(final SortedSet<TreeNodeLock<?>> locks) {
        return new TreeNodeLockRedBlackConcurrentCollector<>(locks, this, this::createLock);
    }

    @Override
    public String toString() {
        return String.format("TreeNodeRedBlackConcurrent={key=%s, value=%s}", node.getKey(), node.getValue());
    }
}
