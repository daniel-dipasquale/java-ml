package com.experimental.data.structure.tree.bst;

import java.util.SortedSet;

public interface TreeNodeConcurrent<TKey, TValue, TNode extends TreeNode<TKey, TValue>> extends TreeNode<TKey, TValue> {
    TreeNode<TKey, TValue> unsafe();

    TNode lockAcquirer(SortedSet<TreeNodeLock<?>> locks); // TODO: change this, the initial lock is incorrect
}
