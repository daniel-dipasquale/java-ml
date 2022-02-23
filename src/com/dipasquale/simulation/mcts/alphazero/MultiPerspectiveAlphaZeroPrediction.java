package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public final class MultiPerspectiveAlphaZeroPrediction<TAction extends Action, TState extends State<TAction, TState>> implements AlphaZeroPrediction<TAction, TState> {
    private final List<SearchNode<TAction, AlphaZeroEdge, TState>> nodes;
    private final float[] policies;
    private final float value;

    private static int calculateIndex(final Arguments arguments, final float index) {
        float value = index / arguments.policyChoiceCount;

        return (int) Math.floor(value * arguments.policyChoiceAvailableCount);
    }

    private static int getPolicyIndex(final Arguments arguments, final int index) {
        if (arguments.isPolicySuperSet) {
            if (index < arguments.valueIndex) {
                return index;
            }

            return index + 1;
        }

        int indexFixed = calculateIndex(arguments, (float) index);

        if (indexFixed < arguments.valueIndex) {
            return indexFixed;
        }

        return indexFixed + 1;
    }

    private static float getPolicy(final Arguments arguments, final int index) {
        int indexFixed = getPolicyIndex(arguments, index);

        if (!arguments.inverseOutputForOpponent || arguments.nextParticipantId == arguments.perspectiveParticipantId) {
            return arguments.output[indexFixed];
        }

        return 1f - arguments.output[indexFixed];
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> float[] createPolicies(final Arguments arguments, final List<SearchNode<TAction, AlphaZeroEdge, TState>> nodes) {
        int size = nodes.size();
        float[] policies = new float[size];
        float total = 0f;

        for (int i = 0; i < size; i++) {
            SearchNode<TAction, AlphaZeroEdge, TState> node = nodes.get(i);
            float policy = getPolicy(arguments, node.getAction().getId());

            policies[i] = policy;
            total += policy;
        }

        for (int i = 0; i < size; i++) {
            policies[i] /= total;
        }

        return policies;
    }

    private static float getValue(final Arguments arguments) {
        if (arguments.currentParticipantId == -1) {
            return 0f;
        }

        if (!arguments.inverseOutputForOpponent || arguments.currentParticipantId == arguments.perspectiveParticipantId) {
            return arguments.output[arguments.valueIndex];
        }

        return -arguments.output[arguments.valueIndex];
    }

    MultiPerspectiveAlphaZeroPrediction(final int perspectiveParticipantId, final boolean inverseOutputForOpponent, final int valueIndex, final NeatAlphaZeroHeuristicContext<TAction, TState> context, final float[] output) {
        SearchNode<TAction, AlphaZeroEdge, TState> node = context.getNode();
        TState state = node.getState();
        List<SearchNode<TAction, AlphaZeroEdge, TState>> nodes = node.createAllPossibleChildNodes(context.getEdgeFactory());
        int policyChoiceCount = nodes.size();
        int policyChoiceAvailableCount = output.length - 1;

        Arguments arguments = Arguments.builder()
                .nextParticipantId(state.getNextParticipantId())
                .currentParticipantId(state.getLastAction().getParticipantId())
                .perspectiveParticipantId(perspectiveParticipantId)
                .inverseOutputForOpponent(inverseOutputForOpponent)
                .isPolicySuperSet(policyChoiceCount <= policyChoiceAvailableCount)
                .policyChoiceCount((float) policyChoiceCount)
                .policyChoiceAvailableCount((float) policyChoiceAvailableCount)
                .valueIndex(valueIndex)
                .output(output)
                .build();

        this.nodes = nodes;
        this.policies = createPolicies(arguments, nodes);
        this.value = getValue(arguments);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class Arguments {
        private final int nextParticipantId;
        private final int currentParticipantId;
        private final int perspectiveParticipantId;
        private final boolean inverseOutputForOpponent;
        private final boolean isPolicySuperSet;
        private final float policyChoiceCount;
        private final float policyChoiceAvailableCount;
        private final int valueIndex;
        private final float[] output;
    }
}