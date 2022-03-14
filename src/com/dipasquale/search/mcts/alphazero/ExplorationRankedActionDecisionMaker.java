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
        float total = 0f;

        for (RankedAction<TAction, TState> rankedAction : rankedActions) {
            total += rankedAction.getEfficiency();
        }

        ProbabilityClassifier<SearchNode<TAction, AlphaZeroEdge, TState>> probabilityClassifier = new ProbabilityClassifier<>();

        for (int i = 1, c = rankedActions.size(); i < c; i++) {
            RankedAction<TAction, TState> rankedAction = rankedActions.get(i);
            float probability = rankedAction.getEfficiency() / total;

            probabilityClassifier.addProbabilityFor(probability, rankedAction.getNode());
        }

        probabilityClassifier.addRemainingProbabilityFor(rankedActions.get(0).getNode());

        return probabilityClassifier.get(randomSupport.next());
    }
}
