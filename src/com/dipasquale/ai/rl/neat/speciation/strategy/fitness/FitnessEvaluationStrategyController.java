package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public final class FitnessEvaluationStrategyController implements FitnessEvaluationStrategy {
    private final Collection<FitnessEvaluationStrategy> strategies;

    @Override
    public void evaluate(final FitnessEvaluationContext context) {
        for (FitnessEvaluationStrategy strategy : strategies) {
            strategy.evaluate(context);
        }
    }
}
