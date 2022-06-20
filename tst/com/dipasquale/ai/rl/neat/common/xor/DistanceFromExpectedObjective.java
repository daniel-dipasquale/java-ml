package com.dipasquale.ai.rl.neat.common.xor;

import com.dipasquale.ai.rl.neat.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.NeatActivator;
import com.dipasquale.ai.rl.neat.NeatTrainingAssessor;
import com.dipasquale.ai.rl.neat.common.NeatObjective;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuronMemory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class DistanceFromExpectedObjective implements NeatObjective<IsolatedNeatEnvironment> {
    private static final float[][] INPUTS = {
            {1f, 1f}, // => 0f
            {1f, 0f}, // => 1f
            {0f, 1f}, // => 1f
            {0f, 0f}  // => 0f
    };

    private static final float[] EXPECTED_OUTPUTS = {0f, 1f, 1f, 0f};
    private final IsolatedNeatEnvironment environment = new InternalEnvironment();
    private final NeatTrainingAssessor trainingAssessor = new InternalTrainingAssessor();

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalEnvironment implements IsolatedNeatEnvironment {
        @Serial
        private static final long serialVersionUID = -4986662353963236661L;

        @Override
        public float test(final GenomeActivator genomeActivator) {
            float error = 0f;
            NeatNeuronMemory neuronMemory = genomeActivator.createMemory();

            for (int i = 0; i < INPUTS.length; i++) {
                float[] output = genomeActivator.activate(INPUTS[i], neuronMemory);

                error += (float) Math.pow(EXPECTED_OUTPUTS[i] - output[0], 2D);
            }

            return (float) INPUTS.length - error;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalTrainingAssessor implements NeatTrainingAssessor {
        @Serial
        private static final long serialVersionUID = 4875450427429878808L;

        @Override
        public boolean test(final NeatActivator activator) {
            boolean success = true;
            NeatNeuronMemory neuronMemory = activator.createMemory();

            for (int i = 0; success && i < INPUTS.length; i++) {
                float[] output = activator.activate(INPUTS[i], neuronMemory);
                int comparison = Float.compare(EXPECTED_OUTPUTS[i], (float) Math.round(output[0]));

                success = comparison == 0;
            }

            return success;
        }
    }
}
