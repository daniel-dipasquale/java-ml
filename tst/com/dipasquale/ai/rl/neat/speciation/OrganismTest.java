package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.common.ActivationFunctionIdentity;
import com.dipasquale.ai.common.ActivationFunctionSigmoid;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryDefault;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.test.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class OrganismTest {
    @Test
    public void TEST_1()
            throws IOException, ClassNotFoundException {
        SequentialIdFactory sequentialIdFactory = new SequentialIdFactoryDefault();
        GenomeDefault genome = new GenomeDefault("id", null);
        SequentialId nodeId1 = sequentialIdFactory.create();
        SequentialId nodeId2 = sequentialIdFactory.create();
        InnovationId innovationId = new InnovationId(new DirectedEdge(nodeId1, nodeId2), sequentialIdFactory.create());

        genome.addNode(new NodeGene(nodeId1, NodeGeneType.BIAS, 1.1f, ActivationFunctionIdentity.getInstance()));
        genome.addNode(new NodeGene(nodeId2, NodeGeneType.HIDDEN, 1.2f, ActivationFunctionSigmoid.getInstance()));
        genome.addConnection(new ConnectionGene(innovationId, 0.9f));

        PopulationInfo population = new PopulationInfo();
        Organism test = new Organism(genome, population);
        byte[] bytes = SerializableSupport.serialize(test);
        Organism result = SerializableSupport.deserialize(bytes);

        Assertions.assertNotSame(test, result);
        Assertions.assertEquals(test, result);
        Assertions.assertEquals(test.getMostCompatibleSpecies(), result.getMostCompatibleSpecies());
    }
}
