package com.dipasquale.ai.rl.neat;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeatEvaluatorTrainingPolicies implements NeatEvaluatorTrainingPolicy {
    private final List<NeatEvaluatorTrainingPolicy> trainingPolicies;
    private final NeatEvaluatorTrainingResult defaultResult;

    @Override
    public NeatEvaluatorTrainingResult test(final NeatActivator activator) {
        if (trainingPolicies.isEmpty()) {
            return defaultResult;
        }

        EnumSet<NeatEvaluatorTrainingResult> results = EnumSet.noneOf(NeatEvaluatorTrainingResult.class);

        for (NeatEvaluatorTrainingPolicy trainingPolicy : trainingPolicies) {
            NeatEvaluatorTrainingResult result = trainingPolicy.test(activator);

            switch (result) {
                case RESTART:
                case STOP:
                case WORKING_SOLUTION_FOUND:
                    return result;
            }

            results.add(result);
        }

        if (results.contains(NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE)
                || results.contains(NeatEvaluatorTrainingResult.EVALUATE_FITNESS) && results.contains(NeatEvaluatorTrainingResult.EVOLVE)) {
            return NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE;
        }

        if (results.contains(NeatEvaluatorTrainingResult.EVALUATE_FITNESS)) {
            return NeatEvaluatorTrainingResult.EVALUATE_FITNESS;
        }

        return NeatEvaluatorTrainingResult.EVOLVE;
    }

    public static NeatEvaluatorTrainingPolicies.Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<NeatEvaluatorTrainingPolicy> trainingPolicies = new ArrayList<>();
        private NeatEvaluatorTrainingResult defaultResult = NeatEvaluatorTrainingResult.EVALUATE_FITNESS;

        public Builder add(final NeatEvaluatorTrainingPolicy trainingPolicy) {
            trainingPolicies.add(trainingPolicy);

            return this;
        }

        public Builder defaultResult(final NeatEvaluatorTrainingResult result) {
            defaultResult = result;

            return this;
        }

        public NeatEvaluatorTrainingPolicies build() {
            return new NeatEvaluatorTrainingPolicies(ImmutableList.copyOf(trainingPolicies), defaultResult);
        }
    }
}
