package com.experimental.data.structure.tree.bst;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
final class TreeStateDefault<TKey, TValue, TNode extends TreeNode<TKey, TValue>> implements TreeState<TKey, TValue, TNode> {
    private TNode rootNode;
    private int size;

    @Override
    public boolean isRootNode(final TNode node) {
        return rootNode == node;
    }

    @Override
    public void incrementSize(final int increment) {
        size += increment;
    }
}