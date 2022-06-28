package com.dipasquale.search.mcts.alphazero.proposal;

import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ExplorationRankedActionDecisionMaker<TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements RankedActionDecisionMaker<TAction, TState, TSearchNode> {
    private final RandomSupport randomSupport;

    @Override
    public TSearchNode decide(final int simulations, final int depth, final List<RankedAction<TAction, TState, TSearchNode>> rankedActions) {
        ProbabilityClassifier<TSearchNode> rankedNodeClassifier = new ProbabilityClassifier<>();

        for (RankedAction<TAction, TState, TSearchNode> rankedAction : rankedActions) {
            rankedNodeClassifier.add(rankedAction.getEfficiency(), rankedAction.getSearchNode());
        }

        return rankedNodeClassifier.get(randomSupport.nextFloat());
    }
}
