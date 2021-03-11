//package com.experimental.data.structure.tree.bst;
//
//import com.dipasquale.data.structure.iterator.MultiIterator;
//import com.dipasquale.data.structure.map.NavigableMapBase;
//import com.google.common.collect.ImmutableList;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//
//import java.util.Comparator;
//import java.util.Deque;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.NavigableMap;
//import java.util.Stack;
//
//public abstract class TreeBinarySearchMapBase<TKey, TValue, TNode extends TreeNodeSpecific<TKey, TValue, TNode> & Map.Entry<TKey, TValue>> extends NavigableMapBase<TKey, TValue> implements NavigableMap<TKey, TValue> {
//    private static final TreeNodeChanger NODE_CHANGER = TreeNodeChanger.getInstance();
//    private final NodeFactory<TKey, TValue, TNode> nodeFactory;
//    private final TreeState<TKey, TValue, TNode> state;
//
//    protected TreeBinarySearchMapBase(final Comparator<TKey> comparator, final NodeFactory<TKey, TValue, TNode> nodeFactory, final TreeState<TKey, TValue, TNode> state) {
//        super(comparator);
//        this.nodeFactory = nodeFactory;
//        this.state = state;
//    }
//
//    protected TreeBinarySearchMapBase(final Comparator<TKey> comparator, final NodeFactory<TKey, TValue, TNode> nodeFactory) {
//        this(comparator, nodeFactory, new TreeStateDefault<>());
//    }
//
//    @Override
//    public int size() {
//        return state.getSize();
//    }
//
//    private int compare(final TNode node, final TKey key) {
//        return comparator.compare(node.getKey(), key);
//    }
//
//    private int compare(final TNode a, final TNode b) {
//        return compare(a, b.getKey());
//    }
//
//    private TNode findExactOrClosestNode(final TNode offsetNode, final TKey key, final ClosestNodeDeterminer<TNode> closestNodeDeterminerFromLeft, final ClosestNodeDeterminer<TNode> closestNodeDeterminerFromRight) {
//        TNode node = offsetNode;
//        TNode closestNode = null;
//
//        while (node != null) {
//            int comparison = compare(node, key);
//
//            if (comparison == 0) {
//                return node;
//            }
//
//            if (comparison > 0) {
//                closestNode = closestNodeDeterminerFromLeft.determine(closestNode, node);
//                node = node.getLeft();
//            } else {
//                closestNode = closestNodeDeterminerFromRight.determine(closestNode, node);
//                node = node.getRight();
//            }
//        }
//
//        return closestNode;
//    }
//
//    protected final TNode findExactNode(final TNode offsetNode, final TKey key) {
//        return findExactOrClosestNode(offsetNode, key, (cn, n) -> null, (cn, n) -> null);
//    }
//
//    protected final TNode findExactNode(final TKey key) {
//        return findExactNode(state.getRootNode(), key);
//    }
//
//    protected final TNode findLessThanOrEqualToNode(final TNode offsetNode, final TKey key) {
//        return findExactOrClosestNode(offsetNode, key, (cn, n) -> null, (cn, n) -> cn == null || compare(cn, n) < 0 ? n : cn);
//    }
//
//    protected final TNode findLessThanOrEqualToNode(final TKey key) {
//        return findLessThanOrEqualToNode(state.getRootNode(), key);
//    }
//
//    protected final TNode findGreaterThanOrEqualToNode(final TNode offsetNode, final TKey key) {
//        return findExactOrClosestNode(offsetNode, key, (cn, n) -> cn == null || compare(cn, n) > 0 ? n : cn, (cn, n) -> null);
//    }
//
//    protected final TNode findGreaterThanOrEqualToNode(final TKey key) {
//        return findGreaterThanOrEqualToNode(state.getRootNode(), key);
//    }
//
//    private TNode findMinimumOrMaximumNode(final TNode offsetNode, final NodeNavigator<TNode> nodeNavigator) {
//        TNode node = offsetNode;
//
//        while (nodeNavigator.get(node) != null) {
//            node = nodeNavigator.get(node);
//        }
//
//        return node;
//    }
//
//    protected final TNode findMinimumNode(final TNode offsetNode) {
//        return findMinimumOrMaximumNode(offsetNode, TreeNodeSpecific::getLeft);
//    }
//
//    protected final TNode findMaximumNode(final TNode offsetNode) {
//        return findMinimumOrMaximumNode(offsetNode, TreeNodeSpecific::getRight);
//    }
//
//    private TNode findPredecessorOrSuccessorNode(final TNode offsetNode, final NodeNavigator<TNode> childNodeNavigator, final NodeNavigator<TNode> minimumOrMaximumNodeNavigator) {
//        TNode x = offsetNode;
//
//        if (childNodeNavigator.get(x) != null) {
//            return minimumOrMaximumNodeNavigator.get(childNodeNavigator.get(x));
//        }
//
//        TNode y = x.getParent();
//
//        while (y != null && x == childNodeNavigator.get(y)) {
//            x = y;
//            y = y.getParent();
//        }
//
//        return y;
//    }
//
//    protected final TNode findPredecessorNode(final TNode offsetNode) {
//        return findPredecessorOrSuccessorNode(offsetNode, TreeNodeSpecific::getLeft, this::findMaximumNode);
//    }
//
//    protected final TNode findSuccessorNode(final TNode offsetNode) {
//        return findPredecessorOrSuccessorNode(offsetNode, TreeNodeSpecific::getRight, this::findMinimumNode);
//    }
//
//    @Override
//    public boolean containsKey(final Object key) {
//        return findExactNode((TKey) key) != null;
//    }
//
//    @Override
//    public TValue get(final Object key) {
//        TNode node = findExactNode((TKey) key);
//
//        if (node == null) {
//            return null;
//        }
//
//        return node.getValue();
//    }
//
//    private PutChange<TNode> putNode(final TNode offsetNode, final TKey key, final TValue value) {
//        TNode node = offsetNode;
//
//        for (int comparison = compare(node, key); comparison != 0; comparison = compare(node, key)) {
//            if (comparison > 0) {
//                if (node.getLeft() == null) {
//                    TNode leftNode = nodeFactory.create(key, value);
//
//                    NODE_CHANGER.changeLeft(node, leftNode);
//
//                    return new PutChange<TNode>(leftNode, null, true);
//                }
//
//                node = node.getLeft();
//            } else {
//                if (node.getRight() == null) {
//                    TNode rightNode = nodeFactory.create(key, value);
//
//                    NODE_CHANGER.changeRight(node, rightNode);
//
//                    return new PutChange<TNode>(rightNode, null, true);
//                }
//
//                node = node.getRight();
//            }
//        }
//
//        TValue oldValue = node.getValue();
//
//        node.setValue(value);
//
//        return new PutChange<TNode>(node, oldValue, false);
//    }
//
//    protected PutChange<TNode> putNode(final TKey key, final TValue value) {
//        TNode rootNode = state.getRootNode();
//
//        if (rootNode != null) {
//            PutChange<TNode> change = putNode(rootNode, key, value);
//
//            if (change.isNew()) {
//                state.incrementSize(1);
//            }
//
//            return change;
//        }
//
//        state.setRootNode(nodeFactory.create(key, value));
//        state.incrementSize(1);
//
//        return new PutChange<TNode>(state.getRootNode(), null, true);
//    }
//
//    @Override
//    protected final PutChange<? extends Entry<TKey, TValue>> putEntry(final TKey key, final TValue value) {
//        return putNode(key, value);
//    }
//
//    protected final TNode traverseToLeafNode(final TNode leftNode, final TNode rightNode) {
//        TreeNodeTraversal previous = new TreeNodeTraversal();
//        TreeNodeTraversal current = new TreeNodeTraversal(leftNode, rightNode);
//
//        while (current.leftNode != null && current.rightNode != null) {
//            previous.leftNode = current.leftNode;
//            previous.rightNode = current.rightNode;
//            current.leftNode = current.leftNode.getRight();
//            current.rightNode = current.rightNode.getLeft();
//        }
//
//        if (previous.leftNode != null) {
//            return previous.leftNode;
//        }
//
//        return previous.rightNode;
//    }
//
//    protected void replaceNode(final TNode node, final TNode replacementNode) {
//        if (replacementNode != null && replacementNode.getParent() != node) {
//            if (replacementNode.getParent().getLeft() == replacementNode) {
//                NODE_CHANGER.changeLeft(replacementNode.getParent(), null);
//            } else {
//                NODE_CHANGER.changeRight(replacementNode.getParent(), null);
//            }
//        }
//
//        NODE_CHANGER.replaceWith(node, replacementNode);
//
//        if (state.isRootNode(node)) {
//            NODE_CHANGER.convertToRootNode(replacementNode);
//        }
//    }
//
//    protected TNode removeNode(final TKey key) {
//        TNode node = findExactNode(key);
//
//        if (node != null) {
//            TNode replacementNode = traverseToLeafNode(node.getLeft(), node.getRight());
//
//            replaceNode(node, replacementNode);
//            state.incrementSize(-1);
//
//            if (state.isRootNode(node)) {
//                state.setRootNode(replacementNode);
//            }
//        }
//
//        return node;
//    }
//
//    @Override
//    protected final Entry<TKey, TValue> removeEntry(final TKey key) {
//        return removeNode(key);
//    }
//
//    @Override
//    public void clear() {
//        state.setRootNode(null);
//        state.setSize(0);
//    }
//
//    private TNode getNode(final TKey key, final NodeNavigator<TNode> nodeNavigator) {
//        TNode node = findExactNode(key);
//
//        if (node == null) {
//            return null;
//        }
//
//        return nodeNavigator.get(node);
//    }
//
//    private <T> T convertNode(final TKey key, final NodeFinder<TKey, TNode> nodeFinder, final NodeNavigator<TNode> nodeNavigator, final NodeConverter<TNode, T> nodeConverter) {
//        TNode node = nodeFinder.find(key);
//
//        if (node == null) {
//            return null;
//        }
//
//        node = nodeNavigator.get(node);
//
//        if (node == null) {
//            return null;
//        }
//
//        return nodeConverter.convert(node);
//    }
//
//    private <T> T convertNode(final TKey key, final NodeFinder<TKey, TNode> nodeFinder, final NodeConverter<TNode, T> nodeConverter) {
//        return convertNode(key, nodeFinder, n -> n, nodeConverter);
//    }
//
//    private TNode getPredecessorNode(final TKey key) {
//        return getNode(key, this::findPredecessorNode);
//    }
//
//    public Entry<TKey, TValue> predecessorEntry(final TKey key) {
//        return convertNode(key, this::getPredecessorNode, n -> n);
//    }
//
//    public TKey predecessorKey(final TKey key) {
//        return convertNode(key, this::getPredecessorNode, TreeNode::getKey);
//    }
//
//    private TNode getSuccessorNode(final TKey key) {
//        return getNode(key, this::findSuccessorNode);
//    }
//
//    public Entry<TKey, TValue> successorEntry(final TKey key) {
//        return convertNode(key, this::getSuccessorNode, n -> n);
//    }
//
//    public TKey successorKey(final TKey key) {
//        return convertNode(key, this::getSuccessorNode, TreeNode::getKey);
//    }
//
//    private TNode getMinimumNode(final TKey key) {
//        return getNode(key, this::findMinimumNode);
//    }
//
//    public Entry<TKey, TValue> minimumEntry(final TKey key) {
//        return convertNode(key, this::getMinimumNode, n -> n);
//    }
//
//    public TKey minimumKey(final TKey key) {
//        return convertNode(key, this::getMinimumNode, TreeNode::getKey);
//    }
//
//    private TNode getMaximumNode(final TKey key) {
//        return getNode(key, this::findMaximumNode);
//    }
//
//    public Entry<TKey, TValue> maximumEntry(final TKey key) {
//        return convertNode(key, this::getMaximumNode, n -> n);
//    }
//
//    public TKey maximumKey(final TKey key) {
//        return convertNode(key, this::getMaximumNode, TreeNode::getKey);
//    }
//
//    private <T> T getUsingRootNodeKey(final KeyValueGetter<TKey, T> handler) {
//        TNode rootNode = state.getRootNode();
//
//        if (rootNode == null) {
//            return null;
//        }
//
//        return handler.getValue(rootNode.getKey());
//    }
//
//    @Override
//    public Entry<TKey, TValue> firstEntry() {
//        return getUsingRootNodeKey(this::minimumEntry);
//    }
//
//    @Override
//    public Entry<TKey, TValue> floorEntry(final TKey key) {
//        return convertNode(key, this::findLessThanOrEqualToNode, n -> n);
//    }
//
//    @Override
//    public Entry<TKey, TValue> lowerEntry(final TKey key) {
//        return convertNode(key, this::findLessThanOrEqualToNode, this::findPredecessorNode, n -> n);
//    }
//
//    @Override
//    public Entry<TKey, TValue> higherEntry(final TKey key) {
//        return convertNode(key, this::findGreaterThanOrEqualToNode, this::findSuccessorNode, n -> n);
//    }
//
//    @Override
//    public Entry<TKey, TValue> ceilingEntry(final TKey key) {
//        return convertNode(key, this::findGreaterThanOrEqualToNode, n -> n);
//    }
//
//    @Override
//    public Entry<TKey, TValue> lastEntry() {
//        return getUsingRootNodeKey(this::maximumEntry);
//    }
//
//    @Override
//    protected Iterator<Entry<TKey, TValue>> iterator(final TKey fromKey, final boolean fromInclusive, final TKey toKey, final boolean toInclusive, final boolean ascending) {
//        return null;
//    }
//
//    private Iterator<Entry<TKey, TValue>> iteratorFrom(final TKey fromKey, final NodeNavigator<TNode> nodeNavigator, final boolean ascending, final SubtreePredicate subtreePredicate) {
//        TNode fromNode = findGreaterThanOrEqualToNode(fromKey);
//        ImmutableList.Builder<Iterator<Entry<TKey, TValue>>> iteratorBuilder = ImmutableList.builder();
//
//        if (fromNode != null) {
//            iteratorBuilder.add(new SingleNodeTreeIterator(fromNode));
//            iteratorBuilder.add(new AllNodesTreeIterator(nodeNavigator.get(fromNode), ascending));
//
//            for (fromNode = fromNode.getParent(); fromNode != null; fromNode = fromNode.getParent()) {
//                int comparison = compare(fromNode, fromKey);
//
//                if (subtreePredicate.isWithinRange(comparison)) {
//                    iteratorBuilder.add(new SingleNodeTreeIterator(fromNode));
//                    iteratorBuilder.add(new AllNodesTreeIterator(nodeNavigator.get(fromNode), ascending));
//                }
//            }
//        }
//
//        return new MultiIterator<>(iteratorBuilder.build(), false);
//    }
//
//    @Override
//    protected Iterator<Entry<TKey, TValue>> iteratorFrom(final TKey key, final boolean ascending) {
//        if (ascending) {
//            return iteratorFrom(key, TreeNodeSpecific::getRight, true, c -> c >= 0);
//        }
//
//        return iteratorFrom(key, TreeNodeSpecific::getLeft, false, c -> c <= 0);
//    }
//
//    @Override
//    protected Iterator<Entry<TKey, TValue>> iteratorTo(TKey key, boolean ascending) {
//        return null;
//    }
//
//    @Override
//    protected Iterator<Entry<TKey, TValue>> iterator(final boolean ascending) {
//        return new AllNodesTreeIterator(state.getRootNode(), ascending);
//    }
//
//    @FunctionalInterface
//    public interface NodeFactory<TKey, TValue, TNode> {
//        TNode create(TKey key, TValue value);
//    }
//
//    @FunctionalInterface
//    private interface ClosestNodeDeterminer<TNode> {
//        TNode determine(TNode previousClosestNode, TNode node);
//    }
//
//    @FunctionalInterface
//    private interface NodeNavigator<TNode> {
//        TNode get(TNode node);
//    }
//
//    @FunctionalInterface
//    private interface NodeFinder<TKey, TNode> {
//        TNode find(TKey key);
//    }
//
//    @FunctionalInterface
//    private interface NodeConverter<TNode, TConverted> {
//        TConverted convert(TNode node);
//    }
//
//    @FunctionalInterface
//    private interface KeyValueGetter<TKey, TValue> {
//        TValue getValue(TKey key);
//    }
//
//    @FunctionalInterface
//    private interface SubtreePredicate {
//        boolean isWithinRange(int comparison);
//    }
//
//    @NoArgsConstructor
//    @AllArgsConstructor
//    private final class TreeNodeTraversal {
//        private TNode leftNode;
//        private TNode rightNode;
//    }
//
//    private final class SingleNodeTreeIterator implements Iterator<Entry<TKey, TValue>> {
//        private final Stack<TNode> nodes;
//
//        public SingleNodeTreeIterator(final TNode node) {
//            Stack<TNode> nodes = new Stack<>();
//
//            nodes.push(node);
//
//            this.nodes = nodes;
//        }
//
//        @Override
//        public boolean hasNext() {
//            return !nodes.isEmpty() && nodes.peek() != null;
//        }
//
//        @Override
//        public Entry<TKey, TValue> next() {
//            return nodes.pop();
//        }
//    }
//
//    private final class AllNodesTreeIterator implements Iterator<Entry<TKey, TValue>> {
//        private final Deque<TNode> nodes;
//        private final boolean ascending;
//
//        public AllNodesTreeIterator(final TNode offset, final boolean ascending) {
//            Deque<TNode> nodes = new LinkedList<>();
//
//            nodes.add(offset);
//
//            this.nodes = nodes;
//            this.ascending = ascending;
//        }
//
//        @Override
//        public boolean hasNext() {
//            return nodes.size() > 1 || !nodes.isEmpty() && nodes.peek() != null;
//        }
//
//        private Entry<TKey, TValue> next(final NodeNavigator<TNode> preOrderNodeNavigator, final NodeNavigator<TNode> postOrderNodeNavigator) {
//            TNode node = nodes.pop();
//
//            while (node != null) {
//                nodes.push(node);
//                node = preOrderNodeNavigator.get(node);
//            }
//
//            node = nodes.pop();
//            nodes.push(postOrderNodeNavigator.get(node));
//
//            return node;
//        }
//
//        @Override
//        public Entry<TKey, TValue> next() {
//            if (ascending) {
//                return next(TreeNodeSpecific::getLeft, TreeNodeSpecific::getRight);
//            }
//
//            return next(TreeNodeSpecific::getRight, TreeNodeSpecific::getLeft);
//        }
//    }
//}