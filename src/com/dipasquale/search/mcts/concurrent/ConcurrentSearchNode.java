package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.AbstractSearchNode;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.lock.PromotableReadWriteLock;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public final class ConcurrentSearchNode<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSearchNode<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    static final boolean FAIR_READ_WRITE_LOCK = false;
    private final int numberOfThreads;
    private final Map<Long, Integer> selectedExplorableChildKeys;
    @Getter
    private final Lock selectionResultLock;
    @Getter
    private final ReadWriteLock expansionLock;
    @Getter
    private final Lock simulationResultLock;

    ConcurrentSearchNode(final SearchNodeResult<TAction, TState> result, final TEdge edge, final int numberOfThreads) {
        super(result, edge);
        this.numberOfThreads = numberOfThreads;
        this.selectedExplorableChildKeys = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, numberOfThreads);
        this.selectionResultLock = new ReentrantLock();
        this.expansionLock = new PromotableReadWriteLock(FAIR_READ_WRITE_LOCK);
        this.simulationResultLock = new ReentrantLock();
    }

    private ConcurrentSearchNode(final ConcurrentSearchNode<TAction, TEdge, TState> parent, final SearchNodeResult<TAction, TState> result, final TEdge edge, final int numberOfThreads) {
        super(parent, result, edge);
        this.numberOfThreads = numberOfThreads;
        this.selectedExplorableChildKeys = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, numberOfThreads);
        this.selectionResultLock = new ReentrantLock();
        this.expansionLock = new PromotableReadWriteLock(FAIR_READ_WRITE_LOCK);
        this.simulationResultLock = new ReentrantLock();
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> createChild(final SearchNodeResult<TAction, TState> result, final EdgeFactory<TEdge> edgeFactory) {
        return new ConcurrentSearchNode<>(this, result, edgeFactory.create(), numberOfThreads);
    }

    @Override
    public int getSelectedExplorableChildKey() {
        long threadId = Thread.currentThread().getId();
        Integer key = selectedExplorableChildKeys.get(threadId);

        if (key != null) {
            return key;
        }

        return NO_SELECTED_EXPLORABLE_CHILD_KEY;
    }

    @Override
    public void setSelectedExplorableChildKey(final int key) {
        long threadId = Thread.currentThread().getId();

        if (key != NO_SELECTED_EXPLORABLE_CHILD_KEY) {
            selectedExplorableChildKeys.put(threadId, key);
        } else {
            selectedExplorableChildKeys.remove(threadId);
        }
    }
}
