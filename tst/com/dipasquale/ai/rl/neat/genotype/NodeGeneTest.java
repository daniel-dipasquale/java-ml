/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.sequence.DefaultSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.common.test.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class NodeGeneTest {
    @Test
    public void TEST_1()
            throws IOException, ClassNotFoundException {
        SequentialIdFactory sequentialIdFactory = new DefaultSequentialIdFactory();
        SequentialId nodeId = sequentialIdFactory.create();
        NodeGeneType type = NodeGeneType.HIDDEN;
        float bias = 1.1f;
        ActivationFunction activationFunction = IdentityActivationFunction.getInstance();
        NodeGene test = new NodeGene(nodeId, type, bias, activationFunction);
        byte[] bytes = SerializableSupport.serialize(test);
        NodeGene result = SerializableSupport.deserialize(bytes);

        Assertions.assertNotSame(test, result);
        Assertions.assertEquals(test.hashCode(), result.hashCode());
        Assertions.assertEquals(test, result);
        Assertions.assertEquals(nodeId, result.getId());
        Assertions.assertEquals(test.getId(), result.getId());
        Assertions.assertEquals(type, result.getType());
        Assertions.assertEquals(test.getType(), result.getType());
        Assertions.assertEquals(bias, result.getBias());
        Assertions.assertEquals(test.getBias(), result.getBias());
        Assertions.assertSame(activationFunction, result.getActivationFunction());
        Assertions.assertSame(test.getActivationFunction(), result.getActivationFunction());
    }
}
