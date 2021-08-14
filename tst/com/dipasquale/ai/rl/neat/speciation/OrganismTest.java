package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.sequence.DefaultSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.common.test.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class OrganismTest {
    @Test
    public void TEST_1()
            throws IOException, ClassNotFoundException {
        SequentialIdFactory sequentialIdFactory = new DefaultSequentialIdFactory();
        DefaultGenome genome = new DefaultGenome("id", null);
        SequentialId nodeId1 = sequentialIdFactory.create();
        SequentialId nodeId2 = sequentialIdFactory.create();
        InnovationId innovationId = new InnovationId(new DirectedEdge(nodeId1, nodeId2), sequentialIdFactory.create());

        genome.addNode(new NodeGene(nodeId1, NodeGeneType.BIAS, 1.1f, IdentityActivationFunction.getInstance()));
        genome.addNode(new NodeGene(nodeId2, NodeGeneType.HIDDEN, 1.2f, SigmoidActivationFunction.getInstance()));
        genome.addConnection(new ConnectionGene(innovationId, 0.9f));

        PopulationState population = new PopulationState();
        Organism test = new Organism(genome, population);
        byte[] bytes = SerializableSupport.serialize(test);
        Organism result = SerializableSupport.deserialize(bytes);

        Assertions.assertNotSame(test, result);
        Assertions.assertEquals(test, result);
        Assertions.assertEquals(test.getMostCompatibleSpecies(), result.getMostCompatibleSpecies());
    }
}
