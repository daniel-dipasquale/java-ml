package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.ExplorationProbabilityCalculator;
import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Getter
final class NeuralNetworkAlphaZeroPrediction<TAction extends Action, TState extends State<TAction, TState>> implements AlphaZeroPrediction<TAction, TState> {
    private final float value;
    private final List<SearchNode<TAction, AlphaZeroEdge, TState>> explorableChildren;
    private final float[] policies;

    private static <TAction extends Action, TState extends State<TAction, TState>> Float calculateValueViaHeuristic(final Arguments arguments, final NeuralNetworkAlphaZeroContext<TAction, TState> context) {
        ValueHeuristic<TAction, TState> valueHeuristic = context.getValueHeuristic();
        SearchNode<TAction, AlphaZeroEdge, TState> searchNode = context.getSearchNode();

        if (valueHeuristic == null || arguments.valueIndex >= 0 && !arguments.behaviorTypes.contains(PredictionBehaviorType.VALUE_HEURISTIC_ALLOWED_ON_INTENTIONAL_STATES) && searchNode.getState().isIntentional()) {
            return null;
        }

        return valueHeuristic.estimate(searchNode.getState());
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> Float calculateValue(final Arguments arguments, final NeuralNetworkAlphaZeroContext<TAction, TState> context) {
        if (!arguments.valueIgnored) {
            float value1 = arguments.getOutput(context)[arguments.valueIndex];
            Float value2 = calculateValueViaHeuristic(arguments, context);

            if (!arguments.behaviorTypes.contains(PredictionBehaviorType.VALUE_REVERSED_ON_OPPONENT) || arguments.participantId == arguments.perspectiveParticipantId) {
                if (value2 == null) {
                    return value1;
                }

                return Math.abs(value1 - value2) - 1f;
            }

            if (value2 == null) {
                return -value1;
            }

            return Math.abs(-value1 - value2) - 1f;
        }

        Float value = calculateValueViaHeuristic(arguments, context);

        if (value == null) {
            return 0f;
        }

        return value;
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> float getValue(final Arguments arguments, final NeuralNetworkAlphaZeroContext<TAction, TState> context) {
        if (arguments.depth == 0) {
            return 0f;
        }

        return calculateValue(arguments, context);
    }

    private static int getPolicyIndex(final Arguments arguments, final int index) {
        if (arguments.valueIgnored || index < arguments.valueIndex) {
            return index;
        }

        return index + 1;
    }

    private static float getPolicy(final Arguments arguments, final float policy) {
        if (!arguments.behaviorTypes.contains(PredictionBehaviorType.POLICY_REVERSED_ON_OPPONENT) || arguments.nextParticipantId == arguments.perspectiveParticipantId) {
            return policy;
        }

        return 1f - policy;
    }

    private static float getPolicy(final Arguments arguments, final float[] output, final int index) {
        int indexFixed = getPolicyIndex(arguments, index);

        return getPolicy(arguments, output[indexFixed]);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> float[] createPolicies(final Arguments arguments, final NeuralNetworkAlphaZeroContext<TAction, TState> context, final List<SearchNode<TAction, AlphaZeroEdge, TState>> explorableChildren) {
        float[] policies = new float[explorableChildren.size()];
        float policyTotal = 0f;

        if (!arguments.policyIgnored) {
            float[] output = arguments.getOutput(context);

            for (int i = 0; i < policies.length; i++) {
                SearchNode<TAction, AlphaZeroEdge, TState> explorableChild = explorableChildren.get(i);
                float policy = getPolicy(arguments, output, explorableChild.getAction().getId());

                policies[i] = policy;
                policyTotal += policy;
            }
        } else {
            ExplorationProbabilityCalculator<TAction> policyCalculator = context.getPolicyCalculator();

            for (int i = 0; i < policies.length; i++) {
                SearchNode<TAction, AlphaZeroEdge, TState> explorableChild = explorableChildren.get(i);
                float policy = getPolicy(arguments, policyCalculator.calculate(explorableChild.getAction()));

                policies[i] = policy;
                policyTotal += policy;
            }
        }

        if (Float.compare(policyTotal, 0f) == 0) {
            Arrays.fill(policies, 1f / (float) policies.length);

            return policies;
        }

        if (Float.compare(policyTotal, 1f) != 0) {
            for (int i = 0; i < policies.length; i++) {
                policies[i] /= policyTotal;
            }
        }

        return policies;
    }

    NeuralNetworkAlphaZeroPrediction(final int perspectiveParticipantId, final EnumSet<PredictionBehaviorType> behaviorTypes, final int valueIndex, final NeuralNetworkAlphaZeroContext<TAction, TState> context) {
        SearchNode<TAction, AlphaZeroEdge, TState> searchNode = context.getSearchNode();
        TState state = searchNode.getState();
        List<SearchNode<TAction, AlphaZeroEdge, TState>> explorableChildren = searchNode.createAllPossibleChildNodes(context.getEdgeFactory());

        Arguments arguments = Arguments.builder()
                .perspectiveParticipantId(perspectiveParticipantId)
                .behaviorTypes(behaviorTypes)
                .participantId(state.getParticipantId())
                .nextParticipantId(state.getNextParticipantId())
                .depth(state.getDepth())
                .valueIgnored(valueIndex < 0 || !state.isIntentional())
                .valueIndex(valueIndex)
                .policyIgnored(!state.isNextIntentional())
                .build();

        this.value = getValue(arguments, context);
        this.explorableChildren = explorableChildren;
        this.policies = createPolicies(arguments, context, explorableChildren);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class Arguments {
        private final int perspectiveParticipantId;
        private final EnumSet<PredictionBehaviorType> behaviorTypes;
        private final int participantId;
        private final int nextParticipantId;
        private final int depth;
        private final boolean valueIgnored;
        private final int valueIndex;
        private final boolean policyIgnored;
        @Builder.Default
        private float[] output = null;

        <TAction extends Action, TState extends State<TAction, TState>> float[] getOutput(final NeuralNetworkAlphaZeroContext<TAction, TState> context) {
            if (output == null) {
                output = context.getPredictor().predict(context.getSearchNode());
            }

            return output;
        }
    }
}