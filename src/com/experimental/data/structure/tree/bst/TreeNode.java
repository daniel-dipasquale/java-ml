package com.experimental.data.structure.tree.bst;

public interface TreeNode<TKey, TValue> {
    TKey getKey();

    TValue getValue();

    TValue setValue(TValue value);

    TreeNode<TKey, TValue> getParent();

    void setParent(TreeNode<TKey, TValue> parent);

    TreeNode<TKey, TValue> getLeft();

    void setLeft(TreeNode<TKey, TValue> left);

    TreeNode<TKey, TValue> getRight();

    void setRight(TreeNode<TKey, TValue> right);
}
