package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchEdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ClassicChildrenInitializerSelectionPolicy<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> implements SelectionPolicy<TState, ClassicSearchEdge, TEnvironment> {
    private final SearchEdgeFactory<ClassicSearchEdge> edgeFactory;
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<TState, ClassicSearchEdge, TEnvironment> next(final int simulations, final SearchNode<TState, ClassicSearchEdge, TEnvironment> node) {
        if (!node.isExpanded()) {
            List<SearchNode<TState, ClassicSearchEdge, TEnvironment>> childNodes = node.createAllPossibleChildNodes(edgeFactory);

            randomSupport.shuffle(childNodes);
            node.setUnexploredChildren(childNodes);
            node.setExplorableChildren(new ArrayList<>());
            node.setFullyExploredChildren(new ArrayList<>());
        }

        return null;
    }
}
