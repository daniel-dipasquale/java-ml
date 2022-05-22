package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.common.concurrent.AtomicCoalescingReference;
import com.dipasquale.search.mcts.AbstractSearchNode;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.lock.LockRing;
import com.dipasquale.synchronization.lock.PromotableReadWriteLock;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ConcurrentSearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSearchNode<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private static final boolean FAIR = false;
    private final AtomicCoalescingReference<TState> state;
    private final int numberOfThreads;
    private final Map<Long, Integer> selectedExplorableChildIndexes;
    @Getter
    private final ReadWriteLock expandingLock;
    @Getter
    private final LockRing<ConcurrentSearchNode<TAction, TEdge, TState>, ReadWriteLock> backPropagatingLockRing;
    @Getter
    private final ReadWriteLock backPropagatingLock;

    ConcurrentSearchNode(final TEdge edge, final TState state, final int numberOfThreads) {
        super(edge, state.getLastAction());
        this.state = new AtomicCoalescingReference<>(FAIR, state, this::createState);
        this.numberOfThreads = numberOfThreads;
        this.selectedExplorableChildIndexes = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, numberOfThreads);
        this.expandingLock = new PromotableReadWriteLock(FAIR);
        this.backPropagatingLockRing = LockRing.createInsertionOrder(FAIR, numberOfThreads);
        this.backPropagatingLock = new ReentrantReadWriteLock();
    }

    private ConcurrentSearchNode(final ConcurrentSearchNode<TAction, TEdge, TState> parent, final TAction action, final TEdge edge, final int numberOfThreads) {
        super(parent, action, edge);
        this.state = new AtomicCoalescingReference<>(FAIR, null, this::createState);
        this.numberOfThreads = numberOfThreads;
        this.selectedExplorableChildIndexes = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, numberOfThreads);
        this.expandingLock = new PromotableReadWriteLock(FAIR);
        this.backPropagatingLockRing = LockRing.createInsertionOrder(FAIR, numberOfThreads);
        this.backPropagatingLock = parent.backPropagatingLockRing.createLock(this);
    }

    @Override
    public TState getState() {
        return state.get();
    }

    @Override
    protected void setState(final TState newState) {
        state.set(newState);
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> createChildNode(final TAction action, final EdgeFactory<TEdge> edgeFactory) {
        return new ConcurrentSearchNode<>(this, action, edgeFactory.create(), numberOfThreads);
    }

    @Override
    public int getSelectedExplorableChildKey() {
        long threadId = Thread.currentThread().getId();
        Integer index = selectedExplorableChildIndexes.get(threadId);

        if (index != null) {
            return index;
        }

        return NO_SELECTED_EXPLORABLE_CHILD_KEY;
    }

    @Override
    public void setSelectedExplorableChildKey(final int key) {
        long threadId = Thread.currentThread().getId();

        if (key != NO_SELECTED_EXPLORABLE_CHILD_KEY) {
            selectedExplorableChildIndexes.put(threadId, key);
        } else {
            selectedExplorableChildIndexes.remove(threadId);
        }
    }
}
