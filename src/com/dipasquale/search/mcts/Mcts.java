package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.iterator.FlatIterator;
import com.dipasquale.search.mcts.buffer.Buffer;
import com.dipasquale.search.mcts.proposal.ProposalStrategy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public final class Mcts<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    private final Buffer<TAction, TEdge, TState, TSearchNode> buffer;
    private final SeekStrategy<TAction, TEdge, TState, TSearchNode> seekStrategy;
    private final ProposalStrategy<TAction, TEdge, TState, TSearchNode> proposalStrategy;
    private final List<ResetHandler> resetHandlers;

    private TSearchNode proposeBestNode(final TSearchNode rootSearchNode) {
        int simulations = rootSearchNode.getEdge().getVisited();
        int nextDepth = rootSearchNode.getStateId().getDepth() + 1;
        List<Iterator<TSearchNode>> childSearchNodeIterators = List.of(rootSearchNode.getExplorableChildren().iterator(), rootSearchNode.getFullyExploredChildren().iterator());
        Iterable<TSearchNode> childSearchNodes = () -> FlatIterator.fromIterators(childSearchNodeIterators);

        return proposalStrategy.proposeBestNode(simulations, nextDepth, childSearchNodes);
    }

    public SearchNodeResult<TAction, TState> proposeNext(final SearchNodeResult<TAction, TState> searchNodeResult) {
        TSearchNode rootSearchNode = buffer.provide(searchNodeResult);

        seekStrategy.process(rootSearchNode);

        TSearchNode bestSearchNode = proposeBestNode(rootSearchNode);

        if (bestSearchNode != null) {
            return bestSearchNode.getResult();
        }

        throw new UnableToProposeNextActionException();
    }

    public void reset() {
        for (ResetHandler resetHandler : resetHandlers) {
            resetHandler.reset();
        }
    }
}
