package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.CommonSearchStrategy;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchPolicy;
import com.dipasquale.search.mcts.SearchStrategy;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.State;
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

public final class ConcurrentSearchStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final ParallelEventLoop eventLoop;
    private final ParallelExecutionContext<ConcurrentSearchNode<TAction, TEdge, TState>> executionContext;

    private static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> ParallelExecutionProxyFactory<SearchStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> createProxyFactory(final Arguments<TAction, TEdge, TState> arguments) {
        return (offset, count) -> {
            DistributedSearchPolicy fixedSearchPolicy = new DistributedSearchPolicy(count, arguments.searchPolicy);

            return new CommonSearchStrategy<>(fixedSearchPolicy, arguments.selectionPolicy, arguments.simulationRolloutPolicy, arguments.backPropagationPolicy);
        };
    }

    private static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> ParallelExecutionHandler<SearchStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>, ConcurrentSearchNode<TAction, TEdge, TState>> createHandler() {
        return (id, searchStrategy, rootSearchNode) -> searchStrategy.process(rootSearchNode);
    }

    private ConcurrentSearchStrategy(final ParallelEventLoop eventLoop, final Arguments<TAction, TEdge, TState> arguments) {
        this.eventLoop = eventLoop;
        this.executionContext = eventLoop.createExecutionContext(arguments.searchPolicy.getMaximumSelectionCount(), createProxyFactory(arguments), createHandler());
    }

    public ConcurrentSearchStrategy(final ParallelEventLoop eventLoop, final SearchPolicy searchPolicy, final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ?> backPropagationPolicy) {
        this(eventLoop, new Arguments<>(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy));
    }

    @Override
    public void process(final ConcurrentSearchNode<TAction, TEdge, TState> rootSearchNode) {
        Set<Throwable> uncaughtExceptions = Collections.newSetFromMap(new IdentityHashMap<>());
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
    private static final class Arguments<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
        private final SearchPolicy searchPolicy;
        private final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy;
        private final SimulationRolloutPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy;
        private final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ?> backPropagationPolicy;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DistributedSearchPolicy implements SearchPolicy {
        private final AtomicInteger invokedCount = new AtomicInteger();
        @Getter
        private final int maximumSelectionCount;
        private final SearchPolicy searchPolicy;

        @Override
        public void begin() {
            if (invokedCount.getAndIncrement() == 0) {
                searchPolicy.begin();
            }
        }

        @Override
        public void end() {
            if (invokedCount.decrementAndGet() == 0) {
                searchPolicy.end();
            }
        }
    }
}
