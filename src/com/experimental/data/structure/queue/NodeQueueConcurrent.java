//package com.experimental.data.structure.queue;
//
//import com.google.common.collect.ImmutableList;
//import com.pasqud.concurrent.ConcurrentHandler;
//import com.pasqud.data.structure.queue.NodeBase;
//import com.pasqud.data.structure.queue.NodeLink;
//import com.pasqud.data.structure.queue.NodeQueueBase;
//
//import java.util.List;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//public final class NodeQueueConcurrent<T> extends NodeQueueBase<T, NodeQueueConcurrent.NodeConcurrent<T>> {
//    private static final ConcurrentHandler CONCURRENT_HANDLER = ConcurrentHandler.getInstance();
//    private final boolean shouldPrioritizeWriting;
//    private final ReadWriteLock membershipLock;
//    private final ReadWriteLock startLock;
//    private final ReadWriteLock endLock;
//    private final ReadWriteLock sizeLock;
//
//    public NodeQueueConcurrent(final boolean shouldPrioritizeWriting) {
//        this.shouldPrioritizeWriting = shouldPrioritizeWriting;
//        this.membershipLock = new ReentrantReadWriteLock(!shouldPrioritizeWriting);
//        this.startLock = new ReentrantReadWriteLock(!shouldPrioritizeWriting);
//        this.endLock = new ReentrantReadWriteLock(!shouldPrioritizeWriting);
//        this.sizeLock = new ReentrantReadWriteLock(!shouldPrioritizeWriting);
//    }
//
//    public NodeQueueConcurrent() {
//        this(true);
//    }
//
//    @Override
//    protected NodeQueueConcurrent.NodeConcurrent<T> createUnlinked(final T value, final Object membership) {
//        return new NodeConcurrent<>(value, membership, shouldPrioritizeWriting);
//    }
//
//    @Override
//    public int size() {
//        return CONCURRENT_HANDLER.get(sizeLock, super::size);
//    }
//
//    @Override
//    protected boolean add(final NodeConcurrent<T> node) {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(endLock.readLock())
//                .add(sizeLock.writeLock())
//                .add(node.previousLock.writeLock())
//                .add(node.nextLock.writeLock())
//                .add(end.previousLock.writeLock())
//                .add(end.previous.nextLock.writeLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::add, node);
//    }
//
//    @Override
//    public boolean add(final NodeLink nodeLink) {
//        List<Lock> locks = ImmutableList.of(membershipLock.readLock());
//
//        return CONCURRENT_HANDLER.invoke(locks, super::add, nodeLink);
//    }
//
//    @Override
//    public boolean offer(final NodeLink nodeLink) {
//        List<Lock> locks = ImmutableList.of(membershipLock.readLock());
//
//        return CONCURRENT_HANDLER.invoke(locks, super::offer, nodeLink);
//    }
//
//    @Override
//    protected NodeConcurrent<T> remove(final NodeConcurrent<T> node) {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(sizeLock.writeLock())
//                .add(node.next.previousLock.writeLock())
//                .add(node.previous.nextLock.writeLock())
//                .add(node.previousLock.writeLock())
//                .add(node.nextLock.writeLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::remove, node);
//    }
//
//    @Override
//    protected boolean remove(final NodeLink nodeLink) {
//        NodeConcurrent<T> node = (NodeConcurrent<T>) nodeLink;
//
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(membershipLock.readLock())
//                .add(node.previousLock.writeLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::remove, nodeLink);
//    }
//
//    @Override
//    protected boolean reoffer(final NodeConcurrent<T> node) {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(node.previousLock.readLock())
//                .add(node.nextLock.readLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::reoffer, node);
//    }
//
//    @Override
//    public boolean reoffer(final NodeLink nodeLink) {
//        List<Lock> locks = ImmutableList.of(membershipLock.readLock());
//
//        return CONCURRENT_HANDLER.invoke(locks, super::reoffer, nodeLink);
//    }
//
//    @Override
//    public NodeLink poll() {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(startLock.readLock())
//                .add(endLock.readLock())
//                .add(start.nextLock.readLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::poll);
//    }
//
//    @Override
//    public NodeLink peek() {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(startLock.readLock())
//                .add(endLock.readLock())
//                .add(start.nextLock.readLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::peek);
//    }
//
//    @Override
//    public NodeLink last() {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(startLock.readLock())
//                .add(endLock.readLock())
//                .add(end.previousLock.readLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::last);
//    }
//
//    @Override
//    protected NodeConcurrent<T> previous(final NodeConcurrent<T> node) {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(startLock.readLock())
//                .add(node.previousLock.readLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::previous, node);
//    }
//
//    @Override
//    public NodeLink previous(final NodeLink nodeLink) {
//        List<Lock> locks = ImmutableList.of(membershipLock.readLock());
//
//        return CONCURRENT_HANDLER.invoke(locks, super::previous, nodeLink);
//    }
//
//    @Override
//    protected NodeConcurrent<T> next(final NodeConcurrent<T> node) {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(endLock.readLock())
//                .add(node.nextLock.readLock())
//                .build();
//
//        return CONCURRENT_HANDLER.invoke(locks, super::next, node);
//    }
//
//    @Override
//    public NodeLink next(final NodeLink nodeLink) {
//        List<Lock> locks = ImmutableList.of(membershipLock.readLock());
//
//        return CONCURRENT_HANDLER.invoke(locks, super::next, nodeLink);
//    }
//
//    @Override
//    public T getValue(final NodeLink nodeLink) {
//        List<Lock> locks = ImmutableList.of(membershipLock.readLock());
//
//        return CONCURRENT_HANDLER.invoke(locks, super::getValue, nodeLink);
//    }
//
//    @Override
//    public void clear() {
//        List<Lock> locks = ImmutableList.<Lock>builder()
//                .add(membershipLock.writeLock())
//                .add(startLock.writeLock())
//                .add(endLock.writeLock())
//                .add(sizeLock.writeLock())
//                .build();
//
//        CONCURRENT_HANDLER.invoke(locks, super::clear);
//    }
//
//    static final class NodeConcurrent<T> extends NodeBase<T, NodeConcurrent<T>> {
//        private final ReadWriteLock previousLock;
//        private final ReadWriteLock nextLock;
//
//        private NodeConcurrent(final T value, final Object membership, final boolean shouldPrioritizeWriting) {
//            super(value, membership);
//            this.previousLock = new ReentrantReadWriteLock(!shouldPrioritizeWriting);
//            this.nextLock = new ReentrantReadWriteLock(!shouldPrioritizeWriting);
//        }
//    }
//}