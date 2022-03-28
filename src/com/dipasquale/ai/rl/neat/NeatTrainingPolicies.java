package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeatTrainingPolicies implements NeatTrainingPolicy, Serializable {
    @Serial
    private static final long serialVersionUID = 2375384538230484687L;
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

    @Override
    public NeatTrainingPolicy createClone() {
        List<NeatTrainingPolicy> fixedTrainingPolicies = trainingPolicies.stream()
                .map(NeatTrainingPolicy::createClone)
                .collect(Collectors.toList());

        return new NeatTrainingPolicies(fixedTrainingPolicies);
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
