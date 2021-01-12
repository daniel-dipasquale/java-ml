package com.experimental.data.structure.tree.bst;

import java.util.SortedSet;

class TreeNodeLockRedBlackConcurrentCollector<TKey, TValue, TNode extends TreeNodeConcurrent<TKey, TValue, ? super TNode> & TreeNodeRedBlack<TKey, TValue>> extends TreeNodeLockConcurrentCollector<TKey, TValue, TNode, TreeNodeLockRedBlackConcurrent> implements TreeNodeRedBlack<TKey, TValue> {
    private final Wrapper<Boolean> isRedWrapper;

    public TreeNodeLockRedBlackConcurrentCollector(final SortedSet<TreeNodeLock<?>> locks, final TNode node, final TreeNodeLockFactory<TreeNodeLockRedBlackConcurrent> lockFactory) {
        super(locks, node, lockFactory);
        this.isRedWrapper = new Wrapper<>();
    }

    @Override
    public boolean isRed() {
        if (!isRedWrapper.isInitialized) {
            isRedWrapper.isInitialized = true;
            isRedWrapper.value = ((TreeNodeRedBlack<TKey, TValue>) node.unsafe()).isRed();
        }

        return isRedWrapper.value;
    }

    @Override
    public boolean setRed(final boolean red) {
        boolean isRedOld = isRedWrapper.value;

        ensureLock().useIsRedLock();
        isRedWrapper.isInitialized = true;
        isRedWrapper.value = red;

        return isRedOld;
    }
}
