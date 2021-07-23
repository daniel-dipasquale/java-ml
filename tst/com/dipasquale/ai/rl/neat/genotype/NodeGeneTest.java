package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionIdentity;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryDefault;
import com.dipasquale.common.test.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class NodeGeneTest {
    @Test
    public void TEST_1()
            throws IOException, ClassNotFoundException {
        SequentialIdFactory sequentialIdFactory = new SequentialIdFactoryDefault();
        SequentialId nodeId = sequentialIdFactory.create();
        NodeGeneType type = NodeGeneType.HIDDEN;
        float bias = 1.1f;
        ActivationFunction activationFunction = ActivationFunctionIdentity.getInstance();
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
