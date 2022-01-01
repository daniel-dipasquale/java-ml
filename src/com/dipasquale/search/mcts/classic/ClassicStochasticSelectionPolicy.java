package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.SearchEdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ClassicStochasticSelectionPolicy<T extends SearchState> implements SelectionPolicy<T, ClassicSearchEdge> {
    private final SearchEdgeFactory<ClassicSearchEdge> edgeFactory;
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<T, ClassicSearchEdge> next(final int simulations, final SearchNode<T, ClassicSearchEdge> node) {
        List<SearchNode<T, ClassicSearchEdge>> childNodes = node.createAllPossibleChildNodes(edgeFactory);
        int size = childNodes.size();

        if (size == 0) {
            return null;
        }

        int index = randomSupport.next(0, size);
        SearchNode<T, ClassicSearchEdge> childNode = childNodes.get(index);

        childNode.initializeEnvironment();
        node.setChildSelectedIndex(index);

        return childNode;
    }
}
