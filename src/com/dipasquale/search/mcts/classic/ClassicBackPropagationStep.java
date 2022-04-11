package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationStep;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ClassicBackPropagationStep<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> implements BackPropagationStep<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> {
    private static final ClassicBackPropagationStep<?, ?, ?> INSTANCE = new ClassicBackPropagationStep<>();

    public static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> ClassicBackPropagationStep<TAction, TState, TSearchNode> getInstance() {
        return (ClassicBackPropagationStep<TAction, TState, TSearchNode>) INSTANCE;
    }

    @Override
    public Context createContext(final TSearchNode leafSearchNode) {
        return new Context(leafSearchNode.getState().getStatusId());
    }

    @Override
    public void process(final Context context, final TSearchNode currentSearchNode) {
        ClassicEdge currentEdge = currentSearchNode.getEdge();

        currentEdge.increaseVisited();

        if (context.statusId == currentSearchNode.getState().getParticipantId()) {
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
