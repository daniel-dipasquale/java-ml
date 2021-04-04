package com.experimental.data.structure.tree.bst;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TreeNodeChanger {
    private static final TreeNodeChanger INSTANCE = new TreeNodeChanger();

    private static TreeNodeChanger getInstance() {
        return INSTANCE;
    }

    public void convertToRootNode(final TreeNode<?, ?> node) {
        if (node != null) {
            node.setParent(null);
        }
    }

    public <TKey, TValue> void changeLeft(final TreeNode<TKey, TValue> parentNode, final TreeNode<TKey, TValue> leftNode) {
        parentNode.setLeft(leftNode);

        if (leftNode != null) {
            leftNode.setParent(parentNode);
        }
    }

    public <TKey, TValue> void changeRight(final TreeNode<TKey, TValue> parentNode, final TreeNode<TKey, TValue> rightNode) {
        parentNode.setRight(rightNode);

        if (rightNode != null) {
            rightNode.setParent(parentNode);
        }
    }

    public <TKey, TValue> boolean isOnLeft(final TreeNode<TKey, TValue> node) {
        return node.getParent().getLeft() == node;
    }

    public <TKey, TValue> boolean isOnRight(final TreeNode<TKey, TValue> node) {
        return node.getParent().getRight() == node;
    }

    public <TKey, TValue> void replaceWith(final TreeNode<TKey, TValue> node, final TreeNode<TKey, TValue> replacementNode) {
        if (node.getParent() != null) {
            if (isOnLeft(node)) {
                node.getParent().setLeft(replacementNode);
            } else {
                node.getParent().setRight(replacementNode);
            }
        } else if (replacementNode != null) {
            replacementNode.setParent(null);
        }

        if (replacementNode != null) {
            if (replacementNode == node.getLeft()) {
                replacementNode.setLeft(null);
            } else {
                replacementNode.setLeft(node.getLeft());
            }

            if (replacementNode == node.getRight()) {
                replacementNode.setRight(null);
            } else {
                replacementNode.setRight(node.getRight());
            }
        }
    }

    public <TKey, TValue, TNode extends TreeNode<TKey, TValue> & TreeNodeSpecific<TKey, TValue, TNode>> TNode rotateLeft(final TNode node) {
        TNode nodeTemporary = node.getRight();

        changeRight(node, nodeTemporary.getLeft());

        if (node.getParent() == null) {
            convertToRootNode(nodeTemporary);
        } else if (isOnLeft(node)) {
            changeLeft(node.getParent(), nodeTemporary);
        } else {
            changeRight(node.getParent(), nodeTemporary);
        }

        changeLeft(nodeTemporary, node);

        return nodeTemporary;
    }

    public <TKey, TValue, TNode extends TreeNode<TKey, TValue> & TreeNodeSpecific<TKey, TValue, TNode>> TNode rotateRight(final TNode node) {
        TNode nodeTemporary = node.getLeft();

        changeLeft(node, nodeTemporary.getRight());

        if (node.getParent() == null) {
            convertToRootNode(nodeTemporary);
        } else if (isOnRight(node)) {
            changeRight(node.getParent(), nodeTemporary);
        } else {
            changeLeft(node.getParent(), nodeTemporary);
        }

        changeRight(nodeTemporary, node);

        return nodeTemporary;
    }
}
