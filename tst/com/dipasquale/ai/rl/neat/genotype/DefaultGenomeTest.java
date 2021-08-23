package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.sequence.LongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.common.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DefaultGenomeTest {
    @Test
    public void TEST_1()
            throws IOException, ClassNotFoundException {
        SequentialIdFactory sequentialIdFactory = new LongSequentialIdFactory();
        DefaultGenome test = new DefaultGenome("id", null);
        SequentialId nodeId1 = sequentialIdFactory.create();
        SequentialId nodeId2 = sequentialIdFactory.create();
        InnovationId innovationId = new InnovationId(new DirectedEdge(nodeId1, nodeId2), sequentialIdFactory.create());

        test.addNode(new NodeGene(nodeId1, NodeGeneType.BIAS, 1.1f, IdentityActivationFunction.getInstance()));
        test.addNode(new NodeGene(nodeId2, NodeGeneType.HIDDEN, 1.2f, SigmoidActivationFunction.getInstance()));
        test.addConnection(new ConnectionGene(innovationId, 0.9f));

        byte[] bytes = SerializableSupport.serialize(test);
        DefaultGenome result = SerializableSupport.deserialize(bytes);

        Assertions.assertNotSame(test, result);
        Assertions.assertEquals(test, result);
        Assertions.assertEquals(test.getId(), result.getId());
        Assertions.assertEquals(test.getNodes(), result.getNodes());
        Assertions.assertEquals(test.getConnections(), result.getConnections());
        Assertions.assertEquals(test.getComplexity(), result.getComplexity());
    }
}
