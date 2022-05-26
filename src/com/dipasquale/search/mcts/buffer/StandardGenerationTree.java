package com.dipasquale.search.mcts.buffer;

import com.dipasquale.common.factory.data.structure.map.HashMapFactory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;

public final class StandardGenerationTree<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractGenerationTree<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, StandardGenerationTree.StandardGeneration<TAction, TEdge, TState>> {
    private static final HashMapFactory HASH_MAP_FACTORY = HashMapFactory.getInstance();

    public StandardGenerationTree() {
        super(new StandardGeneration<>());
    }

    protected static final class StandardGeneration<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends Generation<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, StandardGeneration<TAction, TEdge, TState>> {
        private StandardGeneration() {
            super(HASH_MAP_FACTORY.create());
        }

        @Override
        protected StandardGeneration<TAction, TEdge, TState> createNext() {
            return new StandardGeneration<>();
        }
    }
}
