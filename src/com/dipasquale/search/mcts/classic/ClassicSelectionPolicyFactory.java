package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassicSelectionPolicyFactory<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> implements ObjectFactory<TraversalPolicy<TState, ClassicEdge, TEnvironment>> {
    private final ClassicChildrenInitializerTraversalPolicy<TState, TEnvironment> childrenInitializerTraversalPolicy;
    private final ConfidenceCalculator<ClassicEdge> confidenceCalculator;

    @Override
    public TraversalPolicy<TState, ClassicEdge, TEnvironment> create() {
        return new ClassicSelectionPolicy<>(childrenInitializerTraversalPolicy, confidenceCalculator);
    }
}
