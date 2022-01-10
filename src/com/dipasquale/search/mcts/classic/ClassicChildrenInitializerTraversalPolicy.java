package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.EdgeFactory;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ClassicChildrenInitializerTraversalPolicy<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> implements TraversalPolicy<TState, ClassicEdge, TEnvironment> {
    private final EdgeFactory<ClassicEdge> edgeFactory;
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<TState, ClassicEdge, TEnvironment> next(final int simulations, final SearchNode<TState, ClassicEdge, TEnvironment> node) {
        if (!node.isExpanded()) {
            List<SearchNode<TState, ClassicEdge, TEnvironment>> childNodes = node.createAllPossibleChildNodes(edgeFactory);

            randomSupport.shuffle(childNodes);
            node.setUnexploredChildren(childNodes);
            node.setExplorableChildren(new ArrayList<>());
            node.setFullyExploredChildren(new ArrayList<>());
        }

        return null;
    }
}
