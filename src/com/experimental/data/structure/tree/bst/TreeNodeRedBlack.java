package com.experimental.data.structure.tree.bst;

public interface TreeNodeRedBlack<TKey, TValue> extends TreeNode<TKey, TValue> {
    boolean isRed();

    boolean setRed(boolean red);
}
