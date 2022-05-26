package com.dipasquale.search.mcts.buffer;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
public interface GenerationFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TGeneration extends Generation<TAction, TEdge, TState, TSearchNode, TGeneration>> {
    TGeneration create(MapFactory mapFactory);
}
