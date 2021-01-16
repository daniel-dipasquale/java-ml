package com.experimental.data.structure.tree.bst;

import com.dipasquale.threading.ComparableLock;

import java.util.concurrent.locks.ReadWriteLock;

class TreeNodeLockRedBlackConcurrent extends TreeNodeLockConcurrent<TreeNodeLockRedBlackConcurrent> {
    private final ReadWriteLock isRedLock;
    private boolean wasIsRedLock;

    public TreeNodeLockRedBlackConcurrent(final ComparableLock comparableLock, final ReadWriteLock parentLock, final ReadWriteLock leftLock, final ReadWriteLock rightLock, final ReadWriteLock isRedLock) {
        super(comparableLock, parentLock, leftLock, rightLock);
        this.isRedLock = isRedLock;
        this.wasIsRedLock = false;
    }

    public void useIsRedLock() {
        if (!wasIsRedLock) {
            wasIsRedLock = true;
            activateLock(isRedLock);
        }
    }

    @Override
    public void lock() {
        super.lock();

        if (wasIsRedLock) {
            isRedLock.writeLock().lock();
        }
    }

    @Override
    public void unlock() {
        if (wasIsRedLock) {
            isRedLock.writeLock().unlock();
        }

        super.unlock();
    }
}
