package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ExplorationRankedActionDecisionMaker<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements RankedActionDecisionMaker<TAction, TState, TSearchNode> {
    private final RandomSupport randomSupport;

    @Override
    public TSearchNode decide(final int simulations, final int depth, final List<RankedAction<TAction, TState, TSearchNode>> rankedActions) {
        ProbabilityClassifier<TSearchNode> rankedNodeClassifier = new ProbabilityClassifier<>();

        for (RankedAction<TAction, TState, TSearchNode> rankedAction : rankedActions) {
            rankedNodeClassifier.add(rankedAction.getEfficiency(), rankedAction.getSearchNode());
        }

        return rankedNodeClassifier.get(randomSupport.next());
    }
}
