package com.experimental.data.structure.tree.bst;

import lombok.NoArgsConstructor;

import java.util.SortedSet;

class TreeNodeLockConcurrentCollector<TKey, TValue, TNode extends TreeNodeConcurrent<TKey, TValue, ? super TNode>, TLock extends TreeNodeLockConcurrent<TLock>> implements TreeNode<TKey, TValue> {
    protected final SortedSet<TreeNodeLock<?>> locks;
    protected final TNode node;
    protected final TreeNodeLockFactory<TLock> lockFactory;
    private final Wrapper<TValue> valueWrapper;
    private final Wrapper<TreeNode<TKey, TValue>> parentWrapper;
    private final Wrapper<TreeNode<TKey, TValue>> leftWrapper;
    private final Wrapper<TreeNode<TKey, TValue>> rightWrapper;
    private TLock lock;

    public TreeNodeLockConcurrentCollector(final SortedSet<TreeNodeLock<?>> locks, final TNode node, final TreeNodeLockFactory<TLock> lockFactory) {
        this.locks = locks;
        this.node = node;
        this.lockFactory = lockFactory;
        this.lock = null;
        this.valueWrapper = new Wrapper<>();
        this.parentWrapper = new Wrapper<>();
        this.leftWrapper = new Wrapper<>();
        this.rightWrapper = new Wrapper<>();
    }

    protected final TLock ensureLock() {
        if (lock == null) {
            lock = lockFactory.create();
            locks.add(lock);
        }

        return lock;
    }

    @Override
    public TKey getKey() {
        return node.getKey();
    }

    @Override
    public TValue getValue() {
        if (!valueWrapper.isInitialized) {
            valueWrapper.isInitialized = true;
            valueWrapper.value = node.unsafe().getValue();
        }

        return valueWrapper.value;
    }

    @Override
    public TValue setValue(final TValue value) {
        TValue valueOld = valueWrapper.value;

        valueWrapper.isInitialized = true;
        valueWrapper.value = value;

        return valueOld;
    }

    protected TreeNode<TKey, TValue> unwrap(final Wrapper<TreeNode<TKey, TValue>> wrapper, final TNode node) {
        if (!wrapper.isInitialized) {
            wrapper.isInitialized = true;

            if (node != null) {
                wrapper.value = node.lockAcquirer(locks);
            } else {
                wrapper.value = null;
            }
        }

        return wrapper.value;
    }

    protected void wrap(final Wrapper<TreeNode<TKey, TValue>> wrapper, final TNode node) {
        wrapper.isInitialized = true;
        wrapper.value = node.lockAcquirer(locks);
    }

    @Override
    public TreeNode<TKey, TValue> getParent() {
        return unwrap(parentWrapper, (TNode) node.unsafe().getParent());
    }

    @Override
    public void setParent(final TreeNode<TKey, TValue> parent) {
        ensureLock().activateParentLock();
        wrap(parentWrapper, (TNode) parent);
    }

    @Override
    public TreeNode<TKey, TValue> getLeft() {
        return unwrap(leftWrapper, (TNode) node.unsafe().getLeft());
    }

    @Override
    public void setLeft(final TreeNode<TKey, TValue> left) {
        ensureLock().activateLeftLock();
        wrap(leftWrapper, (TNode) left);
    }

    @Override
    public TreeNode<TKey, TValue> getRight() {
        return unwrap(rightWrapper, (TNode) node.unsafe().getRight());
    }

    @Override
    public void setRight(final TreeNode<TKey, TValue> right) {
        ensureLock().activateRightLock();
        wrap(rightWrapper, (TNode) right);
    }

    @NoArgsConstructor
    protected static final class Wrapper<T> {
        public boolean isInitialized;
        public T value;
    }
}
