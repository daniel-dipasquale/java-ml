package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.NumberSequentialId;
import com.dipasquale.ai.common.sequence.StrategyNumberSequentialId;
import com.dipasquale.ai.rl.neat.internal.Id;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class NeuronStateTest {
    @Test
    public void TEST_1() {
        NeuronState test = new NeuronState();

        Assertions.assertEquals(0f, test.getValue());
    }

    private static Id createNeuronId(final long id) {
        return new Id(new StrategyNumberSequentialId("neuron", new NumberSequentialId(id)));
    }

    @Test
    public void TEST_2() {
        NeuronState test = new NeuronState();

        test.put(createNeuronId(1L), 1f);
        Assertions.assertEquals(1f, test.getValue());
        test.put(createNeuronId(2L), 0.5f);
        Assertions.assertEquals(1.5f, test.getValue());
        test.put(createNeuronId(1L), 0.25f);
        Assertions.assertEquals(0.75f, test.getValue());
    }

    @Test
    public void TEST_3() {
        NeuronState test = new NeuronState();

        test.put(createNeuronId(1L), 1f);
        Assertions.assertEquals(1f, test.getValue());
        test.clear();
        Assertions.assertEquals(0f, test.getValue());
    }
}
