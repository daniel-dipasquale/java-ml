package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.ActivationFunctionIdentity;
import com.dipasquale.ai.common.ActivationFunctionSigmoid;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryDefault;
import com.dipasquale.common.test.SerializableUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class GenomeDefaultTest {
    @Test
    public void TEST_1()
            throws IOException, ClassNotFoundException {
        SequentialIdFactory sequentialIdFactory = new SequentialIdFactoryDefault();
        GenomeDefault test = new GenomeDefault("id", null);
        SequentialId nodeId1 = sequentialIdFactory.create();
        SequentialId nodeId2 = sequentialIdFactory.create();
        InnovationId innovationId = new InnovationId(new DirectedEdge(nodeId1, nodeId2), sequentialIdFactory.create());

        test.addNode(new NodeGene(nodeId1, NodeGeneType.BIAS, 1.1f, ActivationFunctionIdentity.getInstance()));
        test.addNode(new NodeGene(nodeId2, NodeGeneType.HIDDEN, 1.2f, ActivationFunctionSigmoid.getInstance()));
        test.addConnection(new ConnectionGene(innovationId, 0.9f));

        byte[] bytes = SerializableUtils.serialize(test);
        GenomeDefault result = SerializableUtils.deserialize(bytes);

        Assertions.assertNotSame(test, result);
        Assertions.assertEquals(test, result);
        Assertions.assertEquals(test.getId(), result.getId());
        Assertions.assertEquals(test.getNodes(), result.getNodes());
        Assertions.assertEquals(test.getConnections(), result.getConnections());
        Assertions.assertEquals(test.getComplexity(), result.getComplexity());
    }
}
