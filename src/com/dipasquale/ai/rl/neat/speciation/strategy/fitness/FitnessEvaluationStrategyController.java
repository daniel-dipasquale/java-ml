package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public final class FitnessEvaluationStrategyController implements FitnessEvaluationStrategy {
    private final Collection<FitnessEvaluationStrategy> strategies;

    @Override
    public void calculate(final FitnessEvaluationContext context) {
        for (FitnessEvaluationStrategy strategy : strategies) {
            strategy.calculate(context);
        }
    }
}
