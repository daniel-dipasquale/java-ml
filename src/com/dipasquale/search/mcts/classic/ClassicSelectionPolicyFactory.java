package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassicSelectionPolicyFactory<TAction extends Action, TState extends State<TAction, TState>> implements ObjectFactory<TraversalPolicy<TAction, ClassicEdge, TState>> {
    private final ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy;
    private final ConfidenceCalculator<ClassicEdge> confidenceCalculator;

    @Override
    public TraversalPolicy<TAction, ClassicEdge, TState> create() {
        return new ClassicSelectionPolicy<>(childrenInitializerTraversalPolicy, confidenceCalculator);
    }
}
