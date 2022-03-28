package com.dipasquale.search.mcts.common;

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
public final class ValueHeuristicController<TAction extends Action, TState extends State<TAction, TState>> implements ValueHeuristic<TAction, TState> {
    private final EnumSet<ValueHeuristicPermissionType> permissionTypes;
    private final List<WeightedValueHeuristic<TAction, TState>> weightedValueHeuristics;
    private final float totalWeight;

    @Override
    public float estimate(final TState state) {
        if (weightedValueHeuristics.isEmpty()) {
            return 0f;
        }

        boolean intentional = state.isIntentional();

        if (intentional && !permissionTypes.contains(ValueHeuristicPermissionType.ALLOWED_ON_INTENTIONAL_STATES) || !intentional && !permissionTypes.contains(ValueHeuristicPermissionType.ALLOWED_ON_UNINTENTIONAL_STATES)) {
            return 0f;
        }

        float value = 0f;

        for (WeightedValueHeuristic<TAction, TState> weightedValueHeuristic : weightedValueHeuristics) {
            value += weightedValueHeuristic.valueHeuristic.estimate(state) * weightedValueHeuristic.weight;
        }

        return value / totalWeight;
    }

    public static <TAction extends Action, TState extends State<TAction, TState>> Builder<TAction, TState> builder() {
        return new Builder<>();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class WeightedValueHeuristic<TAction extends Action, TState extends State<TAction, TState>> {
        private final ValueHeuristic<TAction, TState> valueHeuristic;
        private final float weight;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder<TAction extends Action, TState extends State<TAction, TState>> {
        private static final EnumSet<ValueHeuristicPermissionType> EMPTY_PERMISSION_TYPE = EnumSet.noneOf(ValueHeuristicPermissionType.class);
        @Setter(AccessLevel.PRIVATE)
        private EnumSet<ValueHeuristicPermissionType> permissionTypes = null;
        private final List<WeightedValueHeuristic<TAction, TState>> weightedValueHeuristics = new ArrayList<>();
        private float totalWeight = 0f;

        public Builder<TAction, TState> permissionTypes(final EnumSet<ValueHeuristicPermissionType> permissionTypes) {
            setPermissionTypes(permissionTypes);

            return this;
        }

        public Builder<TAction, TState> addValueHeuristic(final ValueHeuristic<TAction, TState> valueHeuristic, final float weight) {
            if (Float.compare(weight, 0f) > 0) {
                weightedValueHeuristics.add(new WeightedValueHeuristic<>(valueHeuristic, weight));
                totalWeight += weight;
            }

            return this;
        }

        public ValueHeuristicController<TAction, TState> build() {
            EnumSet<ValueHeuristicPermissionType> fixedPermissionTypes = Optional.ofNullable(permissionTypes)
                    .map(EnumSet::copyOf)
                    .orElse(EMPTY_PERMISSION_TYPE);

            List<WeightedValueHeuristic<TAction, TState>> copiedWeightedValueHeuristics = List.copyOf(weightedValueHeuristics);

            return new ValueHeuristicController<>(fixedPermissionTypes, copiedWeightedValueHeuristics, totalWeight);
        }
    }
}
