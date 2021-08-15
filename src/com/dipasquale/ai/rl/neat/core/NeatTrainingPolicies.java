package com.dipasquale.ai.rl.neat.core;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeatTrainingPolicies implements NeatTrainingPolicy {
    private final List<NeatTrainingPolicy> trainingPolicies;
    private final NeatTrainingResult defaultResult;

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        if (trainingPolicies.isEmpty()) {
            return defaultResult;
        }

        EnumSet<NeatTrainingResult> results = EnumSet.noneOf(NeatTrainingResult.class);

        for (NeatTrainingPolicy trainingPolicy : trainingPolicies) {
            NeatTrainingResult result = trainingPolicy.test(activator);

            switch (result) {
                case RESTART:
                case SOLUTION_NOT_FOUND:
                case WORKING_SOLUTION_FOUND:
                    return result;
            }

            results.add(result);
        }

        if (results.contains(NeatTrainingResult.EVALUATE_FITNESS_AND_EVOLVE)
                || results.contains(NeatTrainingResult.EVALUATE_FITNESS) && results.contains(NeatTrainingResult.EVOLVE)) {
            return NeatTrainingResult.EVALUATE_FITNESS_AND_EVOLVE;
        }

        if (results.contains(NeatTrainingResult.EVALUATE_FITNESS)) {
            return NeatTrainingResult.EVALUATE_FITNESS;
        }

        return NeatTrainingResult.EVOLVE;
    }

    @Override
    public void complete() {
        trainingPolicies.forEach(NeatTrainingPolicy::complete);
    }

    public static NeatTrainingPolicies.Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<NeatTrainingPolicy> trainingPolicies = new ArrayList<>();
        private NeatTrainingResult defaultResult = NeatTrainingResult.SOLUTION_NOT_FOUND;

        public Builder add(final NeatTrainingPolicy trainingPolicy) {
            trainingPolicies.add(trainingPolicy);

            return this;
        }

        public Builder defaultResult(final NeatTrainingResult result) {
            defaultResult = result;

            return this;
        }

        public NeatTrainingPolicies build() {
            return new NeatTrainingPolicies(ImmutableList.copyOf(trainingPolicies), defaultResult);
        }
    }
}
