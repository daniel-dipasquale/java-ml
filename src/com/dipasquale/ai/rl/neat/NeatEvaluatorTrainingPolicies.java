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

    @Override
    public NeatEvaluatorTrainingResult test(final NeatActivator activator) {
        EnumSet<NeatEvaluatorTrainingResult> results = EnumSet.noneOf(NeatEvaluatorTrainingResult.class);

        for (NeatEvaluatorTrainingPolicy trainingPolicy : trainingPolicies) {
            NeatEvaluatorTrainingResult result = trainingPolicy.test(activator);

            switch (result) {
                case RESTART:
                    return NeatEvaluatorTrainingResult.RESTART;

                case STOP:
                    return NeatEvaluatorTrainingResult.STOP;
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

        public void add(final NeatEvaluatorTrainingPolicy trainingPolicy) {
            trainingPolicies.add(trainingPolicy);
        }

        public NeatEvaluatorTrainingPolicies build() {
            return new NeatEvaluatorTrainingPolicies(ImmutableList.copyOf(trainingPolicies));
        }
    }
}
