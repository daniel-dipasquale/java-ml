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
final class ExplorationRankedActionDecisionMaker<TAction extends Action, TState extends State<TAction, TState>> implements RankedActionDecisionMaker<TAction, TState> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<TAction, AlphaZeroEdge, TState> decide(final int simulations, final int depth, final List<RankedAction<TAction, TState>> rankedActions) {
        ProbabilityClassifier<SearchNode<TAction, AlphaZeroEdge, TState>> rankedNodeClassifier = new ProbabilityClassifier<>();

        for (RankedAction<TAction, TState> rankedAction : rankedActions) {
            rankedNodeClassifier.add(rankedAction.getEfficiency(), rankedAction.getSearchNode());
        }

        return rankedNodeClassifier.get(randomSupport.next());
    }
}
