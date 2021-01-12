package com.experimental.data.structure.tree.bst;

public interface TreeState<TKey, TValue, TNode extends TreeNode<TKey, TValue>> {
    TNode getRootNode();

    void setRootNode(TNode rootNode);

    boolean isRootNode(TNode node);

    int getSize();

    void setSize(int size);

    void incrementSize(int increment);
}
