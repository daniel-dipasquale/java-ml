package com.experimental.data.structure.tree.bst;

public interface TreeNodeSpecific<TKey, TValue, TNode extends TreeNode<TKey, TValue>> extends TreeNode<TKey, TValue> {
    TNode getParent();

    TNode getLeft();

    TNode getRight();
}
