package com.dipasquale.search.mcts.buffer.concurrent;

import com.dipasquale.common.factory.data.structure.map.HashMapFactory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.buffer.AbstractGenerationTree;
import com.dipasquale.search.mcts.buffer.Generation;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;

public final class ConcurrentGenerationTree<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractGenerationTree<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ConcurrentGenerationTree.ConcurrentGeneration<TAction, TEdge, TState>> {
    private static final HashMapFactory HASH_MAP_FACTORY = HashMapFactory.getInstance();

    public ConcurrentGenerationTree() {
        super(new ConcurrentGeneration<>());
    }

    protected static final class ConcurrentGeneration<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends Generation<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ConcurrentGeneration<TAction, TEdge, TState>> {
        private ConcurrentGeneration() {
            super(HASH_MAP_FACTORY.create());
        }

        @Override
        protected ConcurrentGeneration<TAction, TEdge, TState> createNext() {
            return new ConcurrentGeneration<>();
        }
    }
}
