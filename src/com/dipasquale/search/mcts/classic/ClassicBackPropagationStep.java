package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationStep;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ClassicBackPropagationStep<TAction extends Action, TState extends State<TAction, TState>> implements BackPropagationStep<TAction, ClassicEdge, TState, ClassicBackPropagationStep.Context> {
    private static final ClassicBackPropagationStep<?, ?> INSTANCE = new ClassicBackPropagationStep<>();

    public static <TAction extends Action, TState extends State<TAction, TState>> ClassicBackPropagationStep<TAction, TState> getInstance() {
        return (ClassicBackPropagationStep<TAction, TState>) INSTANCE;
    }

    @Override
    public Context createContext(final SearchNode<TAction, ClassicEdge, TState> leafNode) {
        return new Context(leafNode.getState().getStatusId());
    }

    @Override
    public void process(final Context context, final SearchNode<TAction, ClassicEdge, TState> currentNode) {
        ClassicEdge currentEdge = currentNode.getEdge();

        currentEdge.increaseVisited();

        if (context.statusId == currentNode.getAction().getParticipantId()) {
            currentEdge.increaseWon();
        } else if (context.statusId == MonteCarloTreeSearch.DRAWN_STATUS_ID) {
            currentEdge.increaseDrawn();
        } else if (context.statusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
            currentEdge.increaseUnfinished();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Context {
        private final int statusId;
    }
}
