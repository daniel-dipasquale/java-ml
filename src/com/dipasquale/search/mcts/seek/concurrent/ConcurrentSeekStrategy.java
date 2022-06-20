package com.dipasquale.search.mcts.seek.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNodeExplorer;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNodeExplorer;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.seek.AbstractSeekStrategy;
import com.dipasquale.search.mcts.seek.SeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import com.dipasquale.synchronization.event.loop.ParallelExecutionContext;
import com.dipasquale.synchronization.event.loop.ParallelExecutionHandler;
import com.dipasquale.synchronization.event.loop.ParallelExecutionProxyFactory;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.StrategyWaitHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConcurrentSeekStrategy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> implements SeekStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final ParallelEventLoop eventLoop;
    private final ParallelExecutionContext<ConcurrentSearchNode<TAction, TEdge, TState>> executionContext;

    private static <TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> ParallelExecutionProxyFactory<SeekStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> createProxyFactory(final InternalArguments<TAction, TEdge, TState> arguments) {
        return (workerId, count) -> {
            InternalSeekPolicy fixedSearchPolicy = new InternalSeekPolicy(count, arguments.seekPolicy);

            return new InternalSeekStrategy<>(fixedSearchPolicy, arguments.selectionPolicy, arguments.simulationPolicy, arguments.backPropagationPolicy, ConcurrentSearchNodeExplorer.getInstance());
        };
    }

    private static <TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> ParallelExecutionHandler<SeekStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>, ConcurrentSearchNode<TAction, TEdge, TState>> createHandler() {
        return (id, searchStrategy, rootSearchNode) -> searchStrategy.process(rootSearchNode);
    }

    private ConcurrentSeekStrategy(final ParallelEventLoop eventLoop, final InternalArguments<TAction, TEdge, TState> arguments) {
        this.eventLoop = eventLoop;
        this.executionContext = eventLoop.createExecutionContext(arguments.seekPolicy.getMaximumSelectionCount(), createProxyFactory(arguments), createHandler());
    }

    public ConcurrentSeekStrategy(final ParallelEventLoop eventLoop, final SeekPolicy seekPolicy, final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationPolicy, final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> backPropagationPolicy) {
        this(eventLoop, new InternalArguments<>(seekPolicy, selectionPolicy, simulationPolicy, backPropagationPolicy));
    }

    @Override
    public void process(final ConcurrentSearchNode<TAction, TEdge, TState> rootSearchNode) {
        Set<Throwable> uncaughtExceptions = Collections.newSetFromMap(Collections.synchronizedMap(new IdentityHashMap<>()));
        InteractiveWaitHandle interactiveWaitHandle = eventLoop.queue(executionContext, rootSearchNode, uncaughtExceptions::add);
        StrategyWaitHandle strategyWaitHandle = new StrategyWaitHandle(interactiveWaitHandle, uncaughtExceptions);

        try {
            strategyWaitHandle.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("The thread was interrupted when processing the root search node", e);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalArguments<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> {
        private final SeekPolicy seekPolicy;
        private final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy;
        private final SimulationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationPolicy;
        private final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> backPropagationPolicy;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalSeekPolicy implements SeekPolicy {
        private final AtomicInteger invokedCount = new AtomicInteger();
        @Getter
        private final int maximumSelectionCount;
        private final SeekPolicy seekPolicy;

        @Override
        public void begin() {
            if (invokedCount.getAndIncrement() == 0) {
                seekPolicy.begin();
            }
        }

        @Override
        public void end() {
            if (invokedCount.decrementAndGet() == 0) {
                seekPolicy.end();
            }
        }
    }

    private static final class InternalSeekStrategy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSeekStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
        private InternalSeekStrategy(final SeekPolicy seekPolicy, final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationPolicy, final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> backPropagationPolicy, final SearchNodeExplorer<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> searchNodeExplorer) {
            super(seekPolicy, selectionPolicy, simulationPolicy, backPropagationPolicy, searchNodeExplorer);
        }
    }
}
