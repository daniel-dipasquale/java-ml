package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.AbstractSearchNode;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.MappedThreadIndex;
import com.dipasquale.synchronization.MappedThreadStorage;
import com.dipasquale.synchronization.lock.PromotableReadWriteLock;
import lombok.Getter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractConcurrentSearchNode<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSearchNode<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> implements ConcurrentSearchNode<TAction, TEdge, TState> {
    private static final boolean FAIR_READ_WRITE_LOCK = false;
    private final MappedThreadStorage<Integer> selectedExplorableChildKeys;
    @Getter
    private final Lock selectionResultLock;
    @Getter
    private final ReadWriteLock expansionLock;
    @Getter
    private final Lock simulationResultLock;

    protected AbstractConcurrentSearchNode(final ConcurrentSearchNode<TAction, TEdge, TState> parent, final SearchResult<TAction, TState> result, final TEdge edge, final MappedThreadIndex mappedThreadIndex) {
        super(parent, result, edge);
        this.selectedExplorableChildKeys = new MappedThreadStorage<>(mappedThreadIndex, Integer.class);
        this.selectionResultLock = new ReentrantLock();
        this.expansionLock = new PromotableReadWriteLock(FAIR_READ_WRITE_LOCK);
        this.simulationResultLock = new ReentrantLock();
    }

    @Override
    public int getSelectedExplorableChildKey() {
        return selectedExplorableChildKeys.getOrDefault(NO_SELECTED_EXPLORABLE_CHILD_KEY);
    }

    @Override
    public void setSelectedExplorableChildKey(final int key) {
        if (key != NO_SELECTED_EXPLORABLE_CHILD_KEY) {
            selectedExplorableChildKeys.put(key);
        } else {
            selectedExplorableChildKeys.remove();
        }
    }
}
