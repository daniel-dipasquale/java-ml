package com.experimental.data.structure.tree.bst;

import java.util.Comparator;
import java.util.Map;

public abstract class TreeBinarySearchMapRedBlackBase<TKey, TValue, TNode extends TreeNodeRedBlack<TKey, TValue> & TreeNodeSpecific<TKey, TValue, TNode> & Map.Entry<TKey, TValue>> extends TreeBinarySearchMapBase<TKey, TValue, TNode> {
    private static final TreeNodeChanger NODE_CHANGER = TreeNodeChanger.getInstance();
    private final TreeState<TKey, TValue, TNode> state;

    protected TreeBinarySearchMapRedBlackBase(final Comparator<TKey> comparator, final TreeState<TKey, TValue, TNode> state, final NodeFactory<TKey, TValue, TNode> nodeFactory) {
        super(comparator, nodeFactory, state);
        this.state = state;
    }

    private boolean isRed(final TNode node) {
        return node != null && node.isRed();
    }

    private TNode fixNodeInsert(final TNode node) {
        TNode currentNode = node;
        TNode rootNode = state.getRootNode();

        while (isRed(currentNode.getParent())) {
            TNode grandParentNode = currentNode.getParent().getParent();
            TNode rotatedNode = null;

            if (NODE_CHANGER.isOnLeft(currentNode.getParent())) {
                TNode uncleNode = grandParentNode.getRight();

                if (isRed(uncleNode)) {
                    currentNode.getParent().setRed(false);
                    uncleNode.setRed(false);
                    grandParentNode.setRed(true);
                    currentNode = grandParentNode;
                }
                if (NODE_CHANGER.isOnRight(currentNode)) {
                    currentNode = currentNode.getParent();
                    rotatedNode = NODE_CHANGER.rotateLeft(currentNode);
                } else {
                    currentNode.getParent().setRed(false);
                    grandParentNode.setRed(true);
                    rotatedNode = NODE_CHANGER.rotateRight(grandParentNode);
                }
            } else {
                TNode uncleNode = grandParentNode.getLeft();

                if (isRed(uncleNode)) {
                    currentNode.getParent().setRed(false);
                    uncleNode.setRed(false);
                    grandParentNode.setRed(true);
                    currentNode = grandParentNode;
                } else if (NODE_CHANGER.isOnLeft(currentNode)) {
                    currentNode = currentNode.getParent();
                    rotatedNode = NODE_CHANGER.rotateRight(currentNode);
                } else {
                    currentNode.getParent().setRed(false);
                    grandParentNode.setRed(true);
                    rotatedNode = NODE_CHANGER.rotateLeft(grandParentNode);
                }
            }

            if (rotatedNode != null && rotatedNode.getParent() == null) {
                rootNode = rotatedNode;
            }
        }

        rootNode.setRed(false);

        return rootNode;
    }

    @Override
    protected PutChange<TNode> putNode(final TKey key, final TValue value) {
        PutChange<TNode> change = super.putNode(key, value);

        if (change.isNew()) {
            change.getEntry().setRed(true);

            TNode node = fixNodeInsert(change.getEntry());

            if (node.getParent() == null) {
                state.setRootNode(node);
            }
        }

        return change;
    }

    private void fixNodeDelete(final TNode node) { // to fix
        TNode x = node;
        TNode y;

        while (x != state.getRootNode() && !x.isRed()) {
            if (x == x.getParent().getLeft()) {
                y = x.getParent().getRight();

                if (y.isRed()) {
                    y.setRed(false);
                    x.getParent().setRed(true);
                    NODE_CHANGER.rotateLeft(x.getParent());
                    y = x.getParent().getRight();
                }

                if (!(y.getLeft().isRed() || y.getRight().isRed())) {
                    if (!y.getRight().isRed()) {
                        y.getLeft().setRed(false);
                        y.setRed(false);
                        NODE_CHANGER.rotateRight(y);
                        y = x.getParent().getRight();
                    }

                    y.setRed(x.getParent().isRed());
                    x.getParent().setRed(false);
                    y.getRight().setRed(false);
                    NODE_CHANGER.rotateLeft(x.getParent());
                    x = state.getRootNode();
                } else {
                    y.setRed(true);
                    x = x.getParent();
                }
            } else {
                y = x.getParent().getLeft();

                if (y.isRed()) {
                    y.setRed(false);
                    x.getParent().setRed(true);
                    NODE_CHANGER.rotateRight(x.getParent());
                    y = x.getParent().getLeft();
                }

                if (!(y.getRight().isRed() || y.getLeft().isRed())) {
                    if (!y.getLeft().isRed()) {
                        y.getRight().setRed(false);
                        y.setRed(true);
                        NODE_CHANGER.rotateLeft(y);
                        y = x.getParent().getLeft();
                    }

                    y.setRed(x.getParent().isRed());
                    x.getParent().setRed(false);
                    y.getLeft().setRed(false);
                    NODE_CHANGER.rotateLeft(x.getParent());
                    x = state.getRootNode();
                } else {
                    y.setRed(true);
                    x = x.getParent();
                }
            }
        }

        x.setRed(false);
    }

    @Override
    protected TNode removeNode(TKey key) {
        TNode node = super.removeNode(key);

        if (node != null) {
            fixNodeDelete(node);
        }

        return node;
    }
}
