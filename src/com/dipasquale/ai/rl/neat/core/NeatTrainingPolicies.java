package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeatTrainingPolicies implements NeatTrainingPolicy {
    private final List<NeatTrainingPolicy> trainingPolicies;

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        for (NeatTrainingPolicy trainingPolicy : trainingPolicies) {
            NeatTrainingResult result = trainingPolicy.test(activator);

            if (result != NeatTrainingResult.CONTINUE_TRAINING) {
                return result;
            }
        }

        return NeatTrainingResult.STOP_TRAINING;
    }

    @Override
    public void reset() {
        trainingPolicies.forEach(NeatTrainingPolicy::reset);
    }

    public static NeatTrainingPolicies.Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<NeatTrainingPolicy> trainingPolicies = new ArrayList<>();

        public Builder add(final NeatTrainingPolicy trainingPolicy) {
            trainingPolicies.add(trainingPolicy);

            return this;
        }

        public NeatTrainingPolicies build() {
            return new NeatTrainingPolicies(List.copyOf(trainingPolicies));
        }
    }
}
