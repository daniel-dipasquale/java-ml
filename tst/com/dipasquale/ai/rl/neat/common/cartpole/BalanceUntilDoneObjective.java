package com.dipasquale.ai.rl.neat.common.cartpole;

import com.dipasquale.ai.rl.neat.ConfinedNeatEnvironment;
import com.dipasquale.ai.rl.neat.NeatActivator;
import com.dipasquale.ai.rl.neat.NeatTrainingAssessor;
import com.dipasquale.ai.rl.neat.common.NeatObjective;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuronMemory;
import com.dipasquale.common.random.float2.DeterministicRandomSupport;
import com.dipasquale.common.random.float2.RandomSupport;
import com.dipasquale.common.random.float2.concurrent.ThreadLocalUniformRandomSupport;
import com.dipasquale.simulation.cart.pole.CartPoleEnvironment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@Getter
final class BalanceUntilDoneObjective implements NeatObjective<ConfinedNeatEnvironment> {
    private static final RandomSupport RANDOM_SUPPORT = ThreadLocalUniformRandomSupport.getInstance();
    private final ConfinedNeatEnvironment environment;
    private final NeatTrainingAssessor trainingAssessor;

    BalanceUntilDoneObjective(final double timeBalancingGoal, final int validationScenarioCount) {
        this.environment = new InternalEnvironment(timeBalancingGoal);
        this.trainingAssessor = new InternalTrainingAssessor(timeBalancingGoal, validationScenarioCount);
    }

    private static float[] convertToFloat(final double[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }

        return output;
    }

    private static void balanceUntilDone(final NeatNeuralNetwork neuralNetwork, final CartPoleEnvironment cartPole, final double timeBalancingGoal) {
        NeatNeuronMemory neuronMemory = neuralNetwork.createMemory();

        while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), timeBalancingGoal) < 0) {
            float[] input = convertToFloat(cartPole.getState());
            float[] output = neuralNetwork.activate(input, neuronMemory);

            cartPole.stepInDiscrete(output[0]);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalEnvironment implements ConfinedNeatEnvironment {
        @Serial
        private static final long serialVersionUID = -5257999118034158016L;
        private final double timeBalancingGoal;

        @Override
        public float test(final GenomeActivator genomeActivator) {
            CartPoleEnvironment cartPole = CartPoleEnvironment.createRandom(RANDOM_SUPPORT);

            balanceUntilDone(genomeActivator, cartPole, timeBalancingGoal);

            return (float) cartPole.getTimeSpent();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalTrainingAssessor implements NeatTrainingAssessor {
        @Serial
        private static final long serialVersionUID = -8478843070415607082L;
        private final double timeBalancingGoal;
        private final int validationScenarioCount;

        @Override
        public boolean test(final NeatActivator activator) {
            boolean success = true;
            DeterministicRandomSupport randomSupport = new DeterministicRandomSupport((long) validationScenarioCount * 4L);

            for (int i = 0; success && i < validationScenarioCount; i++) {
                CartPoleEnvironment cartPole = CartPoleEnvironment.createRandom(randomSupport);

                balanceUntilDone(activator, cartPole, timeBalancingGoal);

                success = Double.compare(cartPole.getTimeSpent(), timeBalancingGoal) >= 0;
            }

            return success;
        }
    }
}
