package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.buffer.Buffer;
import com.dipasquale.search.mcts.buffer.BufferType;
import com.dipasquale.search.mcts.concurrent.ConcurrencySettings;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicyController;
import com.dipasquale.search.mcts.expansion.intention.IntentRegulatorExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.concurrent.ConcurrentHeuristicEdge;
import com.dipasquale.search.mcts.heuristic.concurrent.ConcurrentHeuristicSearchNodeFactorySelector;
import com.dipasquale.search.mcts.heuristic.expansion.ProbableRewardExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.heuristic.propagation.HeuristicBackPropagationStep;
import com.dipasquale.search.mcts.heuristic.proposal.ExpectedRewardActionEfficiencyCalculator;
import com.dipasquale.search.mcts.heuristic.selection.CPuctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.HeuristicUctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.RosinCPuctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import com.dipasquale.search.mcts.initialization.InitializationContext;
import com.dipasquale.search.mcts.initialization.StandardInitializationContext;
import com.dipasquale.search.mcts.initialization.concurrent.ConcurrentInitializationContext;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationType;
import com.dipasquale.search.mcts.proposal.MaximumEfficiencyProposalStrategy;
import com.dipasquale.search.mcts.seek.ComprehensiveSeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeuristicMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private static final RosinCPuctAlgorithm ROSIN_C_PUCT_ALGORITHM = new RosinCPuctAlgorithm();
    private static final HeuristicUctAlgorithm<?> UCT_ALGORITHM = new HeuristicUctAlgorithm<>(ROSIN_C_PUCT_ALGORITHM);
    private final Mcts<TAction, ?, TState, ?> mcts;

    private static <T extends HeuristicEdge> HeuristicUctAlgorithm<T> createUctAlgorithm(final CPuctAlgorithm cpuctAlgorithm) {
        if (cpuctAlgorithm == null) {
            return (HeuristicUctAlgorithm<T>) UCT_ALGORITHM;
        }

        return new HeuristicUctAlgorithm<>(cpuctAlgorithm);
    }

    private static <TAction extends Action, TEdge extends HeuristicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> Buffer<TAction, TEdge, TState, TSearchNode> createBuffer(final BufferType bufferType, final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory) {
        BufferType fixedBufferType = Objects.requireNonNullElse(bufferType, BufferType.DISABLED);

        return fixedBufferType.create(searchNodeFactory);
    }

    private static <TAction extends Action, TEdge extends HeuristicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createExpansionPolicy(final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic, final Buffer<TAction, TEdge, TState, TSearchNode> buffer) {
        List<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();
        ProbableRewardExpansionPolicy<TAction, TEdge, TState, TSearchNode> probableRewardExpansionPolicy = new ProbableRewardExpansionPolicy<>(rewardHeuristic);
        ExpansionPolicy<TAction, TEdge, TState, TSearchNode> intentionalExpansionPolicy = initializationContext.createIntentionalExpansionPolicy(List.of(probableRewardExpansionPolicy), List.of());

        if (explorationHeuristic != null) {
            ExpansionPolicy<TAction, TEdge, TState, TSearchNode> unintentionalExpansionPolicy = initializationContext.createUnintentionalExpansionPolicy(List.of(probableRewardExpansionPolicy), List.of());
            IntentRegulatorExpansionPolicy<TAction, TEdge, TState, TSearchNode> intentRegulatorExpansionPolicy = new IntentRegulatorExpansionPolicy<>(intentionalExpansionPolicy, unintentionalExpansionPolicy);

            expansionPolicies.add(intentRegulatorExpansionPolicy);
        } else {
            expansionPolicies.add(intentionalExpansionPolicy);
        }

        expansionPolicies.add(buffer::put);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TEdge extends HeuristicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> HeuristicBackPropagationStep<TAction, TEdge, TState, TSearchNode> createBackPropagationStep(final BackPropagationType backPropagationType) {
        BackPropagationType fixedBackPropagationType = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);

        return new HeuristicBackPropagationStep<>(fixedBackPropagationType);
    }

    private static <TAction extends Action, TEdge extends HeuristicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> Mcts<TAction, TEdge, TState, TSearchNode> createMcts(final BufferType bufferType, final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic, final UctAlgorithm<TEdge> uctAlgorithm, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        Buffer<TAction, TEdge, TState, TSearchNode> buffer = createBuffer(bufferType, initializationContext.getSearchNodeFactory());
        ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy = createExpansionPolicy(initializationContext, rewardHeuristic, explorationHeuristic, buffer);
        SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy = initializationContext.createSelectionPolicy(uctAlgorithm, expansionPolicy);
        SimulationPolicy<TAction, TEdge, TState, TSearchNode> simulationPolicy = initializationContext.createSimulationPolicy(expansionPolicy);
        HeuristicBackPropagationStep<TAction, TEdge, TState, TSearchNode> backPropagationStep = createBackPropagationStep(backPropagationType);
        BackPropagationPolicy<TAction, TEdge, TState, TSearchNode> backPropagationPolicy = initializationContext.createBackPropagationPolicy(backPropagationStep, backPropagationObserver);
        SeekStrategy<TAction, TEdge, TState, TSearchNode> seekStrategy = initializationContext.createSearchStrategy(selectionPolicy, simulationPolicy, backPropagationPolicy);
        MaximumEfficiencyProposalStrategy<TAction, TEdge, TState, TSearchNode> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(ExpectedRewardActionEfficiencyCalculator.getInstance());
        List<ResetHandler> resetHandlers = ResetHandler.create(buffer);

        return new Mcts<>(buffer, seekStrategy, proposalStrategy, resetHandlers);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> HeuristicMonteCarloTreeSearch<TAction, TState> create(final ComprehensiveSeekPolicy comprehensiveSeekPolicy, final BufferType bufferType, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic, final CPuctAlgorithm cpuctAlgorithm, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver, final ConcurrencySettings concurrencySettings) {
        if (!ConcurrencySettings.isValid(concurrencySettings)) {
            InitializationContext<TAction, StandardHeuristicEdge, TState, StandardSearchNode<TAction, StandardHeuristicEdge, TState>> initializationContext = new StandardInitializationContext<>(StandardHeuristicEdgeFactory.getInstance(), explorationHeuristic, comprehensiveSeekPolicy);
            UctAlgorithm<StandardHeuristicEdge> uctAlgorithm = createUctAlgorithm(cpuctAlgorithm);
            Mcts<TAction, StandardHeuristicEdge, TState, StandardSearchNode<TAction, StandardHeuristicEdge, TState>> mcts = createMcts(bufferType, initializationContext, rewardHeuristic, explorationHeuristic, uctAlgorithm, backPropagationType, backPropagationObserver);

            return new HeuristicMonteCarloTreeSearch<>(mcts);
        }

        ParallelEventLoop eventLoop = concurrencySettings.getEventLoop();
        SearchNodeFactory<TAction, ConcurrentHeuristicEdge, TState, ConcurrentSearchNode<TAction, ConcurrentHeuristicEdge, TState>> searchNodeFactory = ConcurrentHeuristicSearchNodeFactorySelector.<TAction, TState>getInstance().select(concurrencySettings.getEdgeTraversalLockType(), eventLoop.getThreadIds());
        InitializationContext<TAction, ConcurrentHeuristicEdge, TState, ConcurrentSearchNode<TAction, ConcurrentHeuristicEdge, TState>> initializationContext = new ConcurrentInitializationContext<>(searchNodeFactory, eventLoop, explorationHeuristic, comprehensiveSeekPolicy);
        UctAlgorithm<ConcurrentHeuristicEdge> uctAlgorithm = createUctAlgorithm(cpuctAlgorithm);
        Mcts<TAction, ConcurrentHeuristicEdge, TState, ConcurrentSearchNode<TAction, ConcurrentHeuristicEdge, TState>> mcts = createMcts(bufferType, initializationContext, rewardHeuristic, explorationHeuristic, uctAlgorithm, backPropagationType, backPropagationObserver);

        return new HeuristicMonteCarloTreeSearch<>(mcts);
    }

    @Override
    public SearchResult<TAction, TState> proposeNext(final SearchResult<TAction, TState> searchResult) {
        return mcts.proposeNext(searchResult);
    }

    @Override
    public void reset() {
        mcts.reset();
    }
}
