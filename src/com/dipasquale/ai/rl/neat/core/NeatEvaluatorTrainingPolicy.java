/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.core;

@FunctionalInterface
public interface NeatEvaluatorTrainingPolicy {
    NeatEvaluatorTrainingResult test(NeatActivator activator);

    static NeatEvaluatorTrainingPolicy maximumGenerations(final int maximum, final NeatEvaluatorTrainingResult resultOtherwise) {
        return na -> na.getGeneration() < maximum ? resultOtherwise : NeatEvaluatorTrainingResult.STOP;
    }
}
