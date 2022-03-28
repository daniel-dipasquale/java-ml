package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpansionPolicyController<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ExpansionPolicy<TAction, TEdge, TState> {
    private final List<ExpansionPolicy<TAction, TEdge, TState>> expansionPolicies;

    @Override
    public void expand(final SearchNode<TAction, TEdge, TState> searchNode) {
        for (ExpansionPolicy<TAction, TEdge, TState> expansionPolicy : expansionPolicies) {
            expansionPolicy.expand(searchNode);
        }
    }

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> ExpansionPolicy<TAction, TEdge, TState> provide(final List<ExpansionPolicy<TAction, TEdge, TState>> expansionPolicies) {
        return switch (expansionPolicies.size()) {
            case 0 -> null;

            case 1 -> expansionPolicies.get(0);

            default -> new ExpansionPolicyController<>(expansionPolicies);
        };
    }
}
