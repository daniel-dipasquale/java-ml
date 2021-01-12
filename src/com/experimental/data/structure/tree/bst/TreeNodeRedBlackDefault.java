package com.experimental.data.structure.tree.bst;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

@EqualsAndHashCode
final class TreeNodeRedBlackDefault<TKey, TValue> implements TreeNodeRedBlack<TKey, TValue>, TreeNodeSpecific<TKey, TValue, TreeNodeRedBlackDefault<TKey, TValue>>, Map.Entry<TKey, TValue> {
    private final TreeNode<TKey, TValue> node;
    @Getter
    private boolean isRed;

    public TreeNodeRedBlackDefault(final TreeNode<TKey, TValue> node) {
        this.node = node;
        this.isRed = false;
    }

    @Override
    public TKey getKey() {
        return node.getKey();
    }

    @Override
    public TValue getValue() {
        return node.getValue();
    }

    @Override
    public TValue setValue(final TValue value) {
        return node.setValue(value);
    }

    @Override
    public TreeNodeRedBlackDefault<TKey, TValue> getParent() {
        return (TreeNodeRedBlackDefault<TKey, TValue>) node.getParent();
    }

    @Override
    public void setParent(final TreeNode<TKey, TValue> parent) {
        node.setParent(parent);
    }

    @Override
    public TreeNodeRedBlackDefault<TKey, TValue> getLeft() {
        return (TreeNodeRedBlackDefault<TKey, TValue>) node.getLeft();
    }

    @Override
    public void setLeft(final TreeNode<TKey, TValue> left) {
        node.setLeft(left);
    }

    @Override
    public TreeNodeRedBlackDefault<TKey, TValue> getRight() {
        return (TreeNodeRedBlackDefault<TKey, TValue>) node.getRight();
    }

    @Override
    public void setRight(final TreeNode<TKey, TValue> right) {
        node.setRight(right);
    }

    public boolean setRed(final boolean red) {
        boolean isRedOld = isRed;

        isRed = red;

        return isRedOld;
    }

    @Override
    public String toString() {
        return String.format("TreeNodeRedBlackDefault={key=%s, value=%s}", node.getKey(), node.getValue());
    }
}
