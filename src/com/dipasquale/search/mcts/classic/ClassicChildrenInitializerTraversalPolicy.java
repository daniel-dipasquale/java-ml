package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.EdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchNodeCache;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ClassicChildrenInitializerTraversalPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, ClassicEdge, TState> {
    private final EdgeFactory<ClassicEdge> edgeFactory;
    private final RandomSupport randomSupport;
    private final SearchNodeCache<TAction, ClassicEdge, TState> nodeCache;

    @Override
    public SearchNode<TAction, ClassicEdge, TState> next(final int simulations, final SearchNode<TAction, ClassicEdge, TState> node) {
        if (!node.isExpanded()) {
            List<SearchNode<TAction, ClassicEdge, TState>> childNodes = node.createAllPossibleChildNodes(edgeFactory);

            randomSupport.shuffle(childNodes);
            node.setUnexploredChildren(childNodes);
            node.setExplorableChildren(new ArrayList<>());
            node.setFullyExploredChildren(new ArrayList<>());

            if (nodeCache != null) {
                nodeCache.addChildrenIfApplicable(node);
            }
        }

        return null;
    }
}
