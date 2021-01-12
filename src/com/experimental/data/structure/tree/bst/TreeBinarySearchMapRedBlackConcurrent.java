//package com.pasqud.data.structure;
//
//import com.pasqud.concurrency.ConcurrentId;
//import com.pasqud.common.IdFactory;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//import java.util.stream.Collectors;
//
//public final class TreeBinarySearchMapRedBlackConcurrent<TKey, TValue, TConcurrentId extends Comparable<TConcurrentId>> extends TreeBinarySearchMapRedBlackBase<TKey, TValue> {
//    private static final Comparator<TreeNodeVisit<?, ?>> COMPARATOR = Comparator.comparing(e -> (TreeNodeRedBlackConcurrent<?, ?, ?>) e.getNode());
//    private final IdFactory<ConcurrentId<TConcurrentId>> concurrentIdFactory;
//    private final ReadWriteLock rootNodeLock;
//    private final AtomicInteger size;
//
//    public TreeBinarySearchMapRedBlackConcurrent(final Comparator<TKey> comparator, final IdFactory<ConcurrentId<TConcurrentId>> concurrentIdFactory) {
//        super(comparator, new DefaultTreeState<>());
//        this.concurrentIdFactory = concurrentIdFactory;
//        this.rootNodeLock = new ReentrantReadWriteLock();
//        this.size = new AtomicInteger();
//    }
//
//    @Override
//    protected TreeNodeRedBlackConcurrent<TKey, TValue, TConcurrentId> createNode(final TKey key, final TValue value) {
//        TreeNode<TKey, TValue> node = new TreeNodeBasic<>(key, value);
//        TreeNodeRedBlack<TKey, TValue> nodeRedBlack = new TreeNodeRedBlackBasic<>(node);
//
//        return new TreeNodeRedBlackConcurrent<>(nodeRedBlack, concurrentIdFactory.createId());
//    }
//
//    private void lock(final List<ReadWriteLock> locks, final List<TreeNodeVisit<TKey, TValue>> visiting) {
//        for (ReadWriteLock lock : locks) {
//            lock.writeLock().lock();
//        }
//
//        for (TreeNodeVisit<TKey, TValue> visit : visiting) {
//            TreeNodeRedBlackConcurrent<TKey, TValue, TConcurrentId> node = (TreeNodeRedBlackConcurrent<TKey, TValue, TConcurrentId>) visit.getNode();
//            Set<TreeNodeEdge> edges = visit.getEdges();
//
//            node.lock(edges);
//        }
//    }
//
//    private void unlock(final List<ReadWriteLock> locks, final List<TreeNodeVisit<TKey, TValue>> visiting) {
//        ListIterator<TreeNodeVisit<TKey, TValue>> visitingIterator = visiting.listIterator();
//
//        while (visitingIterator.hasPrevious()) {
//            TreeNodeVisit<TKey, TValue> visit = visitingIterator.previous();
//            TreeNodeRedBlackConcurrent<TKey, TValue, TConcurrentId> node = (TreeNodeRedBlackConcurrent<TKey, TValue, TConcurrentId>) visit.getNode();
//            Set<TreeNodeEdge> edges = visit.getEdges();
//
//            node.unlock(edges);
//        }
//
//        ListIterator<ReadWriteLock> lockIterator = locks.listIterator();
//
//        while (lockIterator.hasPrevious()) {
//            lockIterator.previous().writeLock().unlock();
//        }
//    }
//
//    @Override
//    protected void replaceNode(final TreeNode<TKey, TValue> node, final TreeNode<TKey, TValue> replacementNode) {
//        Map<TKey, TreeNodeVisit<TKey, TValue>> visiting = new HashMap<>();
//        TreeNodeVisitTrackerRedBlack<TKey, TValue> nodeVisitTracker = TreeNodeVisitTrackerRedBlack.createRedBlack(visiting, (TreeNodeRedBlack<TKey, TValue>) node);
//        TreeNodeVisitTrackerRedBlack<TKey, TValue> replacementNodeVisitTracker = TreeNodeVisitTrackerRedBlack.createRedBlack(visiting, (TreeNodeRedBlack<TKey, TValue>) replacementNode);
//
//        super.replaceNode(nodeVisitTracker, replacementNodeVisitTracker);
//        List<ReadWriteLock> locks = new ArrayList<>();
//
//        if (replacementNodeVisitTracker != null && replacementNodeVisitTracker.getParent() == null) {
//            locks.add(rootNodeLock);
//        }
//
//        List<TreeNodeVisit<TKey, TValue>> visitingSorted = visiting.values().stream()
//                .sorted(COMPARATOR)
//                .collect(Collectors.toList());
//
//        try {
//            lock(locks, visitingSorted);
//            super.replaceNode(node, replacementNode);
//        } finally {
//            unlock(locks, visitingSorted);
//        }
//    }
//
//    @NoArgsConstructor
//    private static final class DefaultTreeState<TKey, TValue, TConcurrentId extends Comparable<TConcurrentId>> implements TreeStateRedBlack<TKey, TValue> {
//        @Getter
//        private TreeNodeRedBlackConcurrent<TKey, TValue, TConcurrentId> rootNode;
//        private final AtomicInteger size = new AtomicInteger();
//
//        @Override
//        public void setRootNode(final TreeNodeRedBlack<TKey, TValue> rootNode) {
//            this.rootNode = (TreeNodeRedBlackConcurrent<TKey, TValue, TConcurrentId>) rootNode;
//        }
//
//        @Override
//        public void setRootNode(final TreeNode<TKey, TValue> rootNode) {
//            setRootNode((TreeNodeRedBlack<TKey, TValue>) rootNode);
//        }
//
//        @Override
//        public boolean isRootNode(final TreeNodeRedBlack<TKey, TValue> node) {
//            return rootNode == node;
//        }
//
//        @Override
//        public boolean isRootNode(final TreeNode<TKey, TValue> node) {
//            return isRootNode((TreeNodeRedBlack<TKey, TValue>) node);
//        }
//
//        @Override
//        public int getSize() {
//            return size.get();
//        }
//
//        @Override
//        public void setSize(final int size) {
//            this.size.set(size);
//        }
//
//        @Override
//        public void incrementSize(final int increment) {
//            size.addAndGet(increment);
//        }
//    }
//}
