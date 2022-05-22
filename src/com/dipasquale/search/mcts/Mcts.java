package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.iterator.FlatIterator;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public final class Mcts<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    private final Buffer<TAction, TEdge, TState, TSearchNode> buffer;
    private final SearchStrategy<TAction, TEdge, TState, TSearchNode> searchStrategy;
    private final ProposalStrategy<TAction, TEdge, TState, TSearchNode> proposalStrategy;
    private final List<ResetHandler> resetHandlers;

    public TAction proposeNextAction(final TState state) {
        TSearchNode rootSearchNode = buffer.recallOrCreate(state);

        searchStrategy.process(rootSearchNode);

        int nextDepth = state.getDepth() + 1;
        List<Iterator<TSearchNode>> childSearchNodeIterators = List.of(rootSearchNode.getExplorableChildren().iterator(), rootSearchNode.getFullyExploredChildren().iterator());
        Iterable<TSearchNode> childSearchNodes = () -> FlatIterator.fromIterators(childSearchNodeIterators);
        TSearchNode bestSearchNode = proposalStrategy.proposeBestNode(rootSearchNode.getEdge().getVisited(), nextDepth, childSearchNodes);

        if (bestSearchNode != null) {
            return bestSearchNode.getAction();
        }

        throw new UnableToProposeNextActionException();
    }

    public void reset() {
        for (ResetHandler resetHandler : resetHandlers) {
            resetHandler.reset();
        }
    }
}
