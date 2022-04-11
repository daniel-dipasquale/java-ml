package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class StandardMctsInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TBackPropagationContext> implements MctsInitializationContext<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TBackPropagationContext> {
    private static final MapFactory MAP_FACTORY = new HashMapFactory();
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;

    @Override
    public RandomSupport createRandomSupport() {
        return new UniformRandomSupport();
    }

    @Override
    public MapFactory getMapFactory() {
        return MAP_FACTORY;
    }

    @Override
    public SearchNodeFactory<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> getSearchNodeFactory() {
        return StandardSearchNodeFactory.getInstance();
    }

    @Override
    public SearchStrategy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SearchPolicy searchPolicy, final SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TBackPropagationContext> backPropagationPolicy) {
        return new StandardSearchStrategy<>(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class HashMapFactory implements MapFactory {
        @Override
        public <TKey, TValue> Map<TKey, TValue> create(final Map<TKey, TValue> other) {
            if (other == null) {
                return new HashMap<>();
            }

            return new HashMap<>(other);
        }
    }
}
