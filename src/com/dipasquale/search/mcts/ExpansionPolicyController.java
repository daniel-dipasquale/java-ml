package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpansionPolicyController<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final List<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> expansionPolicies;

    @Override
    public void expand(final TSearchNode searchNode) {
        for (ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy : expansionPolicies) {
            expansionPolicy.expand(searchNode);
        }
    }

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> ExpansionPolicy<TAction, TEdge, TState, TSearchNode> provide(final List<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> expansionPolicies) {
        return switch (expansionPolicies.size()) {
            case 0 -> null;

            case 1 -> expansionPolicies.get(0);

            default -> new ExpansionPolicyController<>(expansionPolicies);
        };
    }
}
