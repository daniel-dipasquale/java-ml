package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.NodeCacheSettings;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchNodeCache;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.SimulationResultObserver;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassicMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private final Mcts<TAction, ClassicEdge, TState> mcts;

    private static <TAction extends Action, TState extends State<TAction, TState>> SearchNodeCache<TAction, ClassicEdge, TState> createNodeCache(final NodeCacheSettings nodeCacheSettings, final EdgeFactory<ClassicEdge> edgeFactory) {
        if (nodeCacheSettings == null) {
            return null;
        }

        return new SearchNodeCache<>(nodeCacheSettings.getParticipants(), edgeFactory);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> List<ResetHandler> createResetHandlers(final SearchNodeCache<TAction, ClassicEdge, TState> nodeCache) {
        if (nodeCache == null) {
            return List.of();
        }

        return List.of(nodeCache::clear);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> ClassicMonteCarloTreeSearch<TAction, TState> createClassic(final ClassicSearchPolicy searchPolicy, final NodeCacheSettings nodeCache, final SelectionConfidenceCalculator<ClassicEdge> selectionConfidenceCalculator, final SimulationResultObserver<TAction, ClassicEdge, TState> simulationResultObserver, final ActionEfficiencyCalculator<TAction, ClassicEdge> actionEfficiencyCalculator) {
        EdgeFactory<ClassicEdge> edgeFactory = ClassicEdgeFactory.getInstance();
        RandomSupport randomSupport = new UniformRandomSupport();
        SearchNodeCache<TAction, ClassicEdge, TState> nodeCacheFixed = createNodeCache(nodeCache, edgeFactory);
        ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy = new ClassicChildrenInitializerTraversalPolicy<>(edgeFactory, randomSupport, nodeCacheFixed);
        ClassicSimulationRolloutPolicy<TAction, TState> simulationRolloutPolicy = ClassicSimulationRolloutPolicy.create(searchPolicy, childrenInitializerTraversalPolicy, randomSupport);
        BackPropagationPolicy<TAction, ClassicEdge, TState, ?> backPropagationPolicy = new BackPropagationPolicy<>(ClassicBackPropagationStep.getInstance(), simulationResultObserver);
        ActionEfficiencyCalculator<TAction, ClassicEdge> actionEfficiencyCalculatorFixed = Objects.requireNonNullElseGet(actionEfficiencyCalculator, () -> new PrevalentActionEfficiencyCalculator<>(2f, 0.5f));
        ClassicSearchNodeProposalStrategy<TAction, ClassicEdge, TState> nodeProposalStrategy = new ClassicSearchNodeProposalStrategy<>(actionEfficiencyCalculatorFixed);
        List<ResetHandler> resetHandlers = createResetHandlers(nodeCacheFixed);

        if (selectionConfidenceCalculator == null) {
            Mcts<TAction, ClassicEdge, TState> mcts = new Mcts<>(searchPolicy, edgeFactory, nodeCacheFixed, simulationRolloutPolicy.createSelection(), simulationRolloutPolicy, backPropagationPolicy, nodeProposalStrategy, resetHandlers);

            return new ClassicMonteCarloTreeSearch<>(mcts);
        }

        ClassicSelectionPolicy<TAction, TState> selectionPolicy = new ClassicSelectionPolicy<>(childrenInitializerTraversalPolicy, selectionConfidenceCalculator);
        Mcts<TAction, ClassicEdge, TState> mcts = new Mcts<>(searchPolicy, edgeFactory, nodeCacheFixed, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, nodeProposalStrategy, resetHandlers);

        return new ClassicMonteCarloTreeSearch<>(mcts);
    }

    @Override
    public TAction proposeNextAction(final TState state) {
        return mcts.proposeNextAction(state);
    }

    @Override
    public void reset() {
        mcts.reset();
    }
}
