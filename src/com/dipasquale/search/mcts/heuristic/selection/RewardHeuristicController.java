package com.dipasquale.search.mcts.heuristic.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RewardHeuristicController<TAction extends Action, TState extends State<TAction, TState>> implements RewardHeuristic<TAction, TState> {
    private final EnumSet<RewardHeuristicPermissionType> permissionTypes;
    private final List<WeightedRewardHeuristic<TAction, TState>> weightedRewardHeuristics;
    private final float totalWeight;

    @Override
    public float estimate(final TState state) {
        if (weightedRewardHeuristics.isEmpty()) {
            return 0f;
        }

        boolean intentional = state.isIntentional();

        if (intentional && !permissionTypes.contains(RewardHeuristicPermissionType.ALLOWED_ON_INTENTIONAL_STATES) || !intentional && !permissionTypes.contains(RewardHeuristicPermissionType.ALLOWED_ON_UNINTENTIONAL_STATES)) {
            return 0f;
        }

        float value = 0f;

        for (WeightedRewardHeuristic<TAction, TState> weightedRewardHeuristic : weightedRewardHeuristics) {
            value += weightedRewardHeuristic.rewardHeuristic.estimate(state) * weightedRewardHeuristic.weight;
        }

        return value / totalWeight;
    }

    public static <TAction extends Action, TState extends State<TAction, TState>> Builder<TAction, TState> builder() {
        return new Builder<>();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class WeightedRewardHeuristic<TAction extends Action, TState extends State<TAction, TState>> {
        private final RewardHeuristic<TAction, TState> rewardHeuristic;
        private final float weight;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder<TAction extends Action, TState extends State<TAction, TState>> {
        private static final EnumSet<RewardHeuristicPermissionType> EMPTY_PERMISSION_TYPE = EnumSet.noneOf(RewardHeuristicPermissionType.class);
        @Setter(AccessLevel.PRIVATE)
        private EnumSet<RewardHeuristicPermissionType> permissionTypes = null;
        private final List<WeightedRewardHeuristic<TAction, TState>> weightedRewardHeuristics = new ArrayList<>();
        private float totalWeight = 0f;

        public Builder<TAction, TState> permissionTypes(final EnumSet<RewardHeuristicPermissionType> permissionTypes) {
            setPermissionTypes(permissionTypes);

            return this;
        }

        public Builder<TAction, TState> addHeuristic(final RewardHeuristic<TAction, TState> rewardHeuristic, final float weight) {
            if (Float.compare(weight, 0f) > 0) {
                weightedRewardHeuristics.add(new WeightedRewardHeuristic<>(rewardHeuristic, weight));
                totalWeight += weight;
            }

            return this;
        }

        public RewardHeuristicController<TAction, TState> build() {
            EnumSet<RewardHeuristicPermissionType> fixedPermissionTypes = Optional.ofNullable(permissionTypes)
                    .map(EnumSet::copyOf)
                    .orElse(EMPTY_PERMISSION_TYPE);

            List<WeightedRewardHeuristic<TAction, TState>> copiedWeightedRewardHeuristics = List.copyOf(weightedRewardHeuristics);

            return new RewardHeuristicController<>(fixedPermissionTypes, copiedWeightedRewardHeuristics, totalWeight);
        }
    }
}
