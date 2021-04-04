package com.experimental.data.structure.tree.bst;

import com.dipasquale.threading.lock.ComparableLock;
import com.experimental.concurrent.ConcurrentHandler;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
abstract class TreeNodeConcurrentBase<TKey, TValue, TNodeUnsafe extends TreeNode<TKey, TValue>, TLock extends TreeNodeLockConcurrent<TLock>, TNode extends TreeNodeConcurrentBase<TKey, TValue, TNodeUnsafe, TLock, TNode>> implements TreeNodeConcurrent<TKey, TValue, TreeNode<TKey, TValue>>, TreeNodeSpecific<TKey, TValue, TNode>, Map.Entry<TKey, TValue> {
    private static final ConcurrentHandler CONCURRENT_HANDLER = ConcurrentHandler.getInstance();
    @EqualsAndHashCode.Include
    protected final TNodeUnsafe node;
    protected final ComparableLock comparableLock;
    protected final ReadWriteLock valueLock;
    protected final ReadWriteLock parentLock;
    protected final ReadWriteLock leftLock;
    protected final ReadWriteLock rightLock;

    protected TreeNodeConcurrentBase(final TNodeUnsafe node, final ComparableLock comparableLock, final boolean shouldWritingBePriority) {
        this.node = node;
        this.comparableLock = comparableLock;
        this.valueLock = new ReentrantReadWriteLock(!shouldWritingBePriority);
        this.parentLock = new ReentrantReadWriteLock(!shouldWritingBePriority);
        this.leftLock = new ReentrantReadWriteLock(!shouldWritingBePriority);
        this.rightLock = new ReentrantReadWriteLock(!shouldWritingBePriority);
    }

    @Override
    public TKey getKey() {
        return node.getKey();
    }

    @Override
    public TValue getValue() {
        return CONCURRENT_HANDLER.get(valueLock, node::getValue);
    }

    @Override
    public TValue setValue(final TValue value) {
        return CONCURRENT_HANDLER.getAndSet(valueLock, node::getValue, node::setValue, value);
    }

    @Override
    public TNode getParent() {
        return (TNode) CONCURRENT_HANDLER.get(parentLock, node::getParent);
    }

    @Override
    public void setParent(final TreeNode<TKey, TValue> parent) {
        CONCURRENT_HANDLER.set(parentLock, node::setParent, parent);
    }

    @Override
    public TNode getLeft() {
        return (TNode) CONCURRENT_HANDLER.get(leftLock, node::getLeft);
    }

    @Override
    public void setLeft(final TreeNode<TKey, TValue> left) {
        CONCURRENT_HANDLER.set(leftLock, node::setLeft, left);
    }

    @Override
    public TNode getRight() {
        return (TNode) CONCURRENT_HANDLER.get(rightLock, node::getRight);
    }

    @Override
    public void setRight(final TreeNode<TKey, TValue> right) {
        CONCURRENT_HANDLER.set(rightLock, node::setRight, right);
    }

    @Override
    public TreeNode<TKey, TValue> unsafe() {
        return node;
    }

    private TreeNodeLockConcurrent<TLock> createLock() {
        return new TreeNodeLockConcurrent<>(comparableLock, parentLock, leftLock, rightLock);
    }

    @Override
    public TreeNode<TKey, TValue> lockAcquirer(final SortedSet<TreeNodeLock<?>> locks) {
        return new TreeNodeLockConcurrentCollector<>(locks, (TNode) this, () -> (TLock) createLock());
    }

    @Override
    public String toString() {
        return String.format("TreeNodeConcurrentBase={key=%s, value=%s}", node.getKey(), node.getValue());
    }
}
