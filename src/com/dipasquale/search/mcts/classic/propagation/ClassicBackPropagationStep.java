package com.dipasquale.search.mcts.classic.propagation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassicBackPropagationStep<TAction extends Action, TEdge extends ClassicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements BackPropagationStep<TAction, TEdge, TState, TSearchNode, ClassicBackPropagationStep.InternalContext> {
    private static final ClassicBackPropagationStep<?, ?, ?, ?> INSTANCE = new ClassicBackPropagationStep<>();

    public static <TAction extends Action, TEdge extends ClassicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> ClassicBackPropagationStep<TAction, TEdge, TState, TSearchNode> getInstance() {
        return (ClassicBackPropagationStep<TAction, TEdge, TState, TSearchNode>) INSTANCE;
    }

    @Override
    public InternalContext createContext(final TSearchNode leafSearchNode) {
        return new InternalContext(leafSearchNode.getState().getStatusId());
    }

    @Override
    public void process(final InternalContext context, final TSearchNode currentSearchNode) {
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
    protected static final class InternalContext {
        private final int statusId;
    }
}
