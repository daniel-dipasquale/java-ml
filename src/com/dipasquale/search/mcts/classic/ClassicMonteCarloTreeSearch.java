package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.buffer.Buffer;
import com.dipasquale.search.mcts.buffer.BufferType;
import com.dipasquale.search.mcts.classic.concurrent.ConcurrentClassicEdge;
import com.dipasquale.search.mcts.classic.concurrent.ConcurrentClassicEdgeFactory;
import com.dipasquale.search.mcts.classic.concurrent.ConcurrentClassicUctAlgorithm;
import com.dipasquale.search.mcts.classic.propagation.ClassicBackPropagationStep;
import com.dipasquale.search.mcts.classic.proposal.PrevalentActionEfficiencyCalculator;
import com.dipasquale.search.mcts.classic.selection.ClassicUctAlgorithm;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicyController;
import com.dipasquale.search.mcts.expansion.intention.IntentRegulatorExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import com.dipasquale.search.mcts.initialization.InitializationContext;
import com.dipasquale.search.mcts.initialization.StandardInitializationContext;
import com.dipasquale.search.mcts.initialization.concurrent.ConcurrentInitializationContext;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.proposal.MaximumEfficiencyProposalStrategy;
import com.dipasquale.search.mcts.seek.FullSeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationRolloutPolicy;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassicMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private final Mcts<TAction, ?, TState, ?> mcts;

    private static <TAction extends Action, TEdge extends ClassicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> Buffer<TAction, TEdge, TState, TSearchNode> createBuffer(final BufferType bufferType, final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext) {
        BufferType fixedBufferType = Objects.requireNonNullElse(bufferType, BufferType.DISABLED);

        return fixedBufferType.create(initializationContext);
    }

    private static <TAction extends Action, TEdge extends ClassicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createExpansionPolicy(final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext, final ExplorationHeuristic<TAction> explorationHeuristic, final Buffer<TAction, TEdge, TState, TSearchNode> buffer) {
        List<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();
        ExpansionPolicy<TAction, TEdge, TState, TSearchNode> intentionalExpansionPolicy = initializationContext.createIntentionalExpansionPolicy(List.of(), List.of());

        if (explorationHeuristic != null) {
            ExpansionPolicy<TAction, TEdge, TState, TSearchNode> unintentionalExpansionPolicy = initializationContext.createUnintentionalExpansionPolicy(List.of(), List.of());
            IntentRegulatorExpansionPolicy<TAction, TEdge, TState, TSearchNode> intentRegulatorExpansionPolicy = new IntentRegulatorExpansionPolicy<>(intentionalExpansionPolicy, unintentionalExpansionPolicy);

            expansionPolicies.add(intentRegulatorExpansionPolicy);
        } else {
            expansionPolicies.add(intentionalExpansionPolicy);
        }

        expansionPolicies.add(buffer::put);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TEdge extends ClassicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> Mcts<TAction, TEdge, TState, TSearchNode> createMcts(final BufferType bufferType, final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext, final UctAlgorithm<TEdge> uctAlgorithm, final ExplorationHeuristic<TAction> explorationHeuristic, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        Buffer<TAction, TEdge, TState, TSearchNode> buffer = createBuffer(bufferType, initializationContext);
        ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy = createExpansionPolicy(initializationContext, explorationHeuristic, buffer);
        SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy = initializationContext.createSelectionPolicy(uctAlgorithm, expansionPolicy);
        SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> simulationRolloutPolicy = initializationContext.createSimulationRolloutPolicy(expansionPolicy);
        BackPropagationPolicy<TAction, TEdge, TState, TSearchNode> backPropagationPolicy = initializationContext.createBackPropagationPolicy(ClassicBackPropagationStep.getInstance(), backPropagationObserver);
        SeekStrategy<TAction, TEdge, TState, TSearchNode> seekStrategy = initializationContext.createSearchStrategy(selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
        MaximumEfficiencyProposalStrategy<TAction, TEdge, TState, TSearchNode> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(PrevalentActionEfficiencyCalculator.getInstance());
        List<ResetHandler> resetHandlers = ResetHandler.create(buffer);

        return new Mcts<>(buffer, seekStrategy, proposalStrategy, resetHandlers);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> ClassicMonteCarloTreeSearch<TAction, TState> create(final FullSeekPolicy searchPolicy, final ExplorationHeuristic<TAction> explorationHeuristic, final BufferType bufferType, final BackPropagationObserver<TAction, TState> backPropagationObserver, final ParallelEventLoop eventLoop) {
        if (eventLoop == null) {
            InitializationContext<TAction, StandardClassicEdge, TState, StandardSearchNode<TAction, StandardClassicEdge, TState>> initializationContext = new StandardInitializationContext<>(StandardClassicEdgeFactory.getInstance(), explorationHeuristic, searchPolicy);
            UctAlgorithm<StandardClassicEdge> uctAlgorithm = ClassicUctAlgorithm.getInstance();
            Mcts<TAction, StandardClassicEdge, TState, StandardSearchNode<TAction, StandardClassicEdge, TState>> mcts = createMcts(bufferType, initializationContext, uctAlgorithm, explorationHeuristic, backPropagationObserver);

            return new ClassicMonteCarloTreeSearch<>(mcts);
        }

        InitializationContext<TAction, ConcurrentClassicEdge, TState, ConcurrentSearchNode<TAction, ConcurrentClassicEdge, TState>> initializationContext = new ConcurrentInitializationContext<>(eventLoop, ConcurrentClassicEdgeFactory.getInstance(), explorationHeuristic, searchPolicy);
        UctAlgorithm<ConcurrentClassicEdge> uctAlgorithm = new ConcurrentClassicUctAlgorithm(eventLoop.getConcurrencyLevel());
        Mcts<TAction, ConcurrentClassicEdge, TState, ConcurrentSearchNode<TAction, ConcurrentClassicEdge, TState>> mcts = createMcts(bufferType, initializationContext, uctAlgorithm, explorationHeuristic, backPropagationObserver);

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
