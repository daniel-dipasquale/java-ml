package com.experimental.data.structure.tree.bst;

import com.dipasquale.concurrent.ConcurrentId;
import lombok.RequiredArgsConstructor;

import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;

@RequiredArgsConstructor
class TreeNodeLockConcurrent<T extends TreeNodeLockConcurrent<T>> implements TreeNodeLock<T> {
    private final ConcurrentId concurrentId;
    private final ReadWriteLock parentLock;
    private final ReadWriteLock leftLock;
    private final ReadWriteLock rightLock;
    private final Stack<ReadWriteLock> initialLocks = new Stack<>(); // TODO: fix this, because initial locks are not going to be done in the order they need to be if they're always here
    private boolean wasParentLockActivated = false;
    private boolean wasLeftLockActivated = false;
    private boolean wasRightLockActivated = false;

    protected void activateLock(final ReadWriteLock lock) {
        lock.readLock().lock();
        initialLocks.push(lock);
    }

    public void activateParentLock() {
        if (!wasParentLockActivated) {
            wasParentLockActivated = true;
            activateLock(parentLock);
        }
    }

    public void activateLeftLock() {
        if (!wasLeftLockActivated) {
            wasLeftLockActivated = true;
            activateLock(leftLock);
        }
    }

    public void activateRightLock() {
        if (!wasRightLockActivated) {
            wasRightLockActivated = true;
            activateLock(rightLock);
        }
    }

    @Override
    public void lock() {
        if (wasParentLockActivated) {
            parentLock.writeLock().lock();
        }

        if (wasLeftLockActivated) {
            leftLock.writeLock().lock();
        }

        if (wasRightLockActivated) {
            rightLock.writeLock().lock();
        }
    }

    @Override
    public void unlock() {
        if (wasRightLockActivated) {
            rightLock.writeLock().unlock();
        }

        if (wasLeftLockActivated) {
            leftLock.writeLock().unlock();
        }

        if (wasParentLockActivated) {
            parentLock.writeLock().unlock();
        }

        for (ReadWriteLock readLock = initialLocks.pop(); !initialLocks.empty(); readLock = initialLocks.pop()) {
            readLock.readLock().unlock();
        }
    }

    @Override
    public int compareTo(final T other) {
        return concurrentId.compareTo(((TreeNodeLockConcurrent<T>) other).concurrentId);
    }
}
