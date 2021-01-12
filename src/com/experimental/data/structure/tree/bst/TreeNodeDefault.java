package com.experimental.data.structure.tree.bst;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
final class TreeNodeDefault<TKey, TValue> implements TreeNode<TKey, TValue>, TreeNodeSpecific<TKey, TValue, TreeNodeDefault<TKey, TValue>>, Map.Entry<TKey, TValue> {
    @EqualsAndHashCode.Include
    private final TKey key;
    protected TreeNodeDefault<TKey, TValue> parent;
    protected TreeNodeDefault<TKey, TValue> left;
    protected TreeNodeDefault<TKey, TValue> right;
    @EqualsAndHashCode.Include
    private TValue value;

    public TreeNodeDefault(final TKey key, final TValue value) {
        this.key = key;
        this.value = value;
        this.parent = null;
        this.left = null;
        this.right = null;
    }

    public TValue setValue(final TValue value) {
        TValue valueOld = this.value;

        this.value = value;

        return valueOld;
    }

    @Override
    public void setParent(final TreeNode<TKey, TValue> parent) {
        this.parent = (TreeNodeDefault<TKey, TValue>) parent;
    }

    @Override
    public void setLeft(final TreeNode<TKey, TValue> left) {
        this.left = (TreeNodeDefault<TKey, TValue>) left;
    }

    @Override
    public void setRight(final TreeNode<TKey, TValue> right) {
        this.right = (TreeNodeDefault<TKey, TValue>) right;
    }

    @Override
    public String toString() {
        return String.format("TreeNodeDefault={key=%s, value=%s}", key, value);
    }
}
