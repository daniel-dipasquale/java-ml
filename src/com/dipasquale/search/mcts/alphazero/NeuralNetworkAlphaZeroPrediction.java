package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;

@Getter
final class NeuralNetworkAlphaZeroPrediction<TAction extends Action, TState extends State<TAction, TState>> implements AlphaZeroPrediction<TAction, TState> {
    private final float value;
    private final List<SearchNode<TAction, AlphaZeroEdge, TState>> nodes;
    private final float[] policies;

    private static <TAction extends Action, TState extends State<TAction, TState>> Float calculateValue(final NeuralNetworkAlphaZeroHeuristicContext<TAction, TState> context) {
        AlphaZeroValueCalculator<TAction, TState> valueCalculator = context.getValueCalculator();

        if (valueCalculator == null) {
            return null;
        }

        SearchNode<TAction, AlphaZeroEdge, TState> node = context.getNode();

        return context.getValueCalculator().calculate(node);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> Float calculateValue(final Arguments arguments, final NeuralNetworkAlphaZeroHeuristicContext<TAction, TState> context) {
        if (!arguments.valueIgnored) {
            float value1 = arguments.output[arguments.valueIndex];
            Float value2 = calculateValue(context);

            if (value2 == null) {
                return value1;
            }

            return Math.abs(value1 - value2) - 1f;
        }

        Float value = calculateValue(context);

        if (value == null) {
            return 0f;
        }

        return value;
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> float getValue(final Arguments arguments, final NeuralNetworkAlphaZeroHeuristicContext<TAction, TState> context) {
        if (arguments.currentParticipantId == MonteCarloTreeSearch.INITIAL_PARTICIPANT_ID) {
            if (arguments.behaviorType.contains(PredictionBehaviorType.VALUE_FOR_INITIAL_STATE_IS_ZERO)) {
                return 0f;
            }

            Float value = calculateValue(context);

            if (value != null) {
                return value;
            }
        }

        if (!arguments.behaviorType.contains(PredictionBehaviorType.INVERSE_VALUE_FOR_OPPONENT) || arguments.currentParticipantId == arguments.perspectiveParticipantId) {
            return calculateValue(arguments, context);
        }

        return -calculateValue(arguments, context);
    }

    private static int getPolicyIndex(final Arguments arguments, final int index) {
        if (arguments.valueIgnored || index < arguments.valueIndex) {
            return index;
        }

        return index + 1;
    }

    private static float getPolicy(final Arguments arguments, final int index) {
        int indexFixed = getPolicyIndex(arguments, index);

        if (!arguments.behaviorType.contains(PredictionBehaviorType.INVERSE_POLICY_FOR_OPPONENT) || arguments.nextParticipantId == arguments.perspectiveParticipantId) {
            return arguments.output[indexFixed];
        }

        return 1f - arguments.output[indexFixed];
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> float[] createPolicies(final Arguments arguments, final NeuralNetworkAlphaZeroHeuristicContext<TAction, TState> context, final List<SearchNode<TAction, AlphaZeroEdge, TState>> nodes) {
        int actionAvailableSize = arguments.policyLength;
        int actualActionSize = nodes.size();
        float[] policies = new float[actualActionSize];
        float policyTotal = 0f;
        AlphaZeroPolicyDistributor<TAction, TState> policyDistributor = context.getPolicyDistributor();

        if (policyDistributor != null) {
            int[] actionIdCounters = new int[actionAvailableSize];

            for (SearchNode<TAction, AlphaZeroEdge, TState> node : nodes) {
                int actionId = node.getAction().getId();

                actionIdCounters[actionId]++;
            }

            float[] actionIdPolicies = new float[actionAvailableSize];

            for (int i = 0; i < actionAvailableSize; i++) {
                int actionCount = actionIdCounters[i];

                if (actionCount > 0) {
                    actionIdPolicies[i] = getPolicy(arguments, i) / (float) actionCount;
                }
            }

            for (int i = 0; i < actualActionSize; i++) {
                SearchNode<TAction, AlphaZeroEdge, TState> node = nodes.get(i);
                int actionId = node.getAction().getId();
                float policy = policyDistributor.distribute(node, actionIdPolicies[actionId]);

                policies[i] = policy;
                policyTotal += policy;
            }
        } else {
            for (int i = 0; i < actualActionSize; i++) {
                SearchNode<TAction, AlphaZeroEdge, TState> node = nodes.get(i);
                int actionId = node.getAction().getId();
                float policy = getPolicy(arguments, actionId);

                policies[i] = policy;
                policyTotal += policy;
            }
        }

        for (int i = 0; i < actualActionSize; i++) {
            policies[i] /= policyTotal;
        }

        return policies;
    }

    NeuralNetworkAlphaZeroPrediction(final int perspectiveParticipantId, final EnumSet<PredictionBehaviorType> behaviorType, final int valueIndex, final NeuralNetworkAlphaZeroHeuristicContext<TAction, TState> context, final float[] output) {
        SearchNode<TAction, AlphaZeroEdge, TState> node = context.getNode();
        TState state = node.getState();
        List<SearchNode<TAction, AlphaZeroEdge, TState>> nodes = node.createAllPossibleChildNodes(context.getEdgeFactory());
        boolean valueIgnored = valueIndex < 0;

        Arguments arguments = Arguments.builder()
                .nextParticipantId(state.getNextParticipantId())
                .currentParticipantId(state.getLastAction().getParticipantId())
                .perspectiveParticipantId(perspectiveParticipantId)
                .behaviorType(behaviorType)
                .valueIgnored(valueIgnored)
                .valueIndex(valueIndex)
                .policyLength(!valueIgnored
                        ? output.length - 1
                        : output.length)
                .output(output)
                .build();

        this.value = getValue(arguments, context);
        this.nodes = nodes;
        this.policies = createPolicies(arguments, context, nodes);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class Arguments {
        private final int nextParticipantId;
        private final int currentParticipantId;
        private final int perspectiveParticipantId;
        private final EnumSet<PredictionBehaviorType> behaviorType;
        private final boolean valueIgnored;
        private final int valueIndex;
        private final int policyLength;
        private final float[] output;
    }
}