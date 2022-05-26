package com.dipasquale.search.mcts.alphazero.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;

@Getter
final class NeuralNetworkAlphaZeroPrediction<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements AlphaZeroPrediction<TAction, TState, TSearchNode> {
    private final float value;
    private final SearchNodeGroup<TAction, AlphaZeroEdge, TState, TSearchNode> explorableChildren;
    private final float[] policies;

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> Float calculateValueViaHeuristic(final Arguments arguments, final NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode> context) {
        RewardHeuristic<TAction, TState> rewardHeuristic = context.getRewardHeuristic();
        TSearchNode searchNode = context.getSearchNode();

        if (rewardHeuristic == null || arguments.valueIndex >= 0 && !arguments.behaviorTypes.contains(PredictionBehaviorType.VALUE_HEURISTIC_ALLOWED_ON_INTENTIONAL_STATES) && searchNode.getState().isIntentional()) {
            return null;
        }

        return rewardHeuristic.estimate(searchNode.getState());
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> Float calculateValue(final Arguments arguments, final NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode> context) {
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

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> float getValue(final Arguments arguments, final NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode> context) {
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
        int fixedIndex = getPolicyIndex(arguments, index);

        return getPolicy(arguments, output[fixedIndex]);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> float[] createPolicies(final Arguments arguments, final NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode> context, final SearchNodeGroup<TAction, AlphaZeroEdge, TState, TSearchNode> explorableChildren) {
        float[] policies = new float[explorableChildren.size()];
        float totalPolicy = 0f;

        if (!arguments.policyIgnored) {
            float[] output = arguments.getOutput(context);

            for (int i = 0; i < policies.length; i++) {
                TSearchNode explorableChild = explorableChildren.getByIndex(i);
                float policy = getPolicy(arguments, output, explorableChild.getAction().getId());

                policies[i] = policy;
                totalPolicy += policy;
            }
        } else {
            ExplorationHeuristic<TAction> explorationHeuristic = context.getExplorationHeuristic();

            for (int i = 0; i < policies.length; i++) {
                TSearchNode explorableChild = explorableChildren.getByIndex(i);
                float policy = getPolicy(arguments, explorationHeuristic.estimate(explorableChild.getAction()));

                policies[i] = policy;
                totalPolicy += policy;
            }
        }

        if (Float.compare(totalPolicy, 0f) == 0) {
            Arrays.fill(policies, 1f / (float) policies.length);

            return policies;
        }

        if (Float.compare(totalPolicy, 1f) != 0) {
            for (int i = 0; i < policies.length; i++) {
                policies[i] /= totalPolicy;
            }
        }

        return policies;
    }

    NeuralNetworkAlphaZeroPrediction(final int perspectiveParticipantId, final EnumSet<PredictionBehaviorType> behaviorTypes, final int valueIndex, final NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode> context) {
        TSearchNode searchNode = context.getSearchNode();
        TState state = searchNode.getState();
        Iterable<TSearchNode> explorableChildrenIterable = searchNode.createAllPossibleChildNodes(context.getEdgeFactory());
        SearchNodeGroup<TAction, AlphaZeroEdge, TState, TSearchNode> explorableChildren = context.getSearchNodeGroupProvider().create(explorableChildrenIterable);

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

        <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> float[] getOutput(final NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode> context) {
            if (output == null) {
                output = context.getPredictor().predict(context.getSearchNode());
            }

            return output;
        }
    }
}