package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.common.ActivationFunctionIdentity;
import com.dipasquale.ai.common.ActivationFunctionSigmoid;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryDefault;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.test.SerializableUtils;
import com.dipasquale.threading.wait.handle.WaitHandle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.function.Consumer;

public final class SpeciesTest {
    private static Context createContext() {
        return new Context() {
            @Override
            public GeneralSupport general() {
                return null;
            }

            @Override
            public NodeGeneSupport nodes() {
                return new NodeGeneSupport() {
                    @Override
                    public NodeGene create(final SequentialId id, final NodeGeneType type) {
                        return null;
                    }

                    @Override
                    public int size(final NodeGeneType type) {
                        return 1;
                    }
                };
            }

            @Override
            public ConnectionGeneSupport connections() {
                return null;
            }

            @Override
            public NeuralNetworkSupport neuralNetwork() {
                return null;
            }

            @Override
            public ParallelismSupport parallelism() {
                return new ParallelismSupport() {
                    @Override
                    public boolean isEnabled() {
                        return false;
                    }

                    @Override
                    public int numberOfThreads() {
                        return 1;
                    }

                    @Override
                    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> action) {
                        return null;
                    }
                };
            }

            @Override
            public RandomSupport random() {
                return null;
            }

            @Override
            public MutationSupport mutation() {
                return null;
            }

            @Override
            public CrossOverSupport crossOver() {
                return null;
            }

            @Override
            public SpeciationSupport speciation() {
                return null;
            }

            @Override
            public void save(final ObjectOutputStream outputStream) {
            }

            @Override
            public void load(final ObjectInputStream inputStream, final StateOverrideSupport override) {
            }
        };
    }

    @Test
    public void TEST_1() throws IOException, ClassNotFoundException {
        SequentialIdFactory sequentialIdFactory = new SequentialIdFactoryDefault();
        GenomeDefault genome = new GenomeDefault("id", null);
        SequentialId nodeId1 = sequentialIdFactory.create();
        SequentialId nodeId2 = sequentialIdFactory.create();
        InnovationId innovationId = new InnovationId(new DirectedEdge(nodeId1, nodeId2), sequentialIdFactory.create());

        genome.addNode(new NodeGene(nodeId1, NodeGeneType.BIAS, 1.1f, ActivationFunctionIdentity.getInstance()));
        genome.addNode(new NodeGene(nodeId2, NodeGeneType.HIDDEN, 1.2f, ActivationFunctionSigmoid.getInstance()));
        genome.addConnection(new ConnectionGene(innovationId, 0.9f));

        PopulationInfo population = new PopulationInfo();

        population.getHistoricalMarkings().initialize(createContext());

        Organism organism = new Organism(genome, population);
        Species test = new Species(organism, population);
        byte[] bytes = SerializableUtils.serialize(test);
        Species result = SerializableUtils.deserialize(bytes);

        Assertions.assertNotSame(test, result);
        Assertions.assertEquals(test, result);
        Assertions.assertEquals(test.getId(), result.getId());
        Assertions.assertEquals(test.getRepresentative(), result.getRepresentative());
        Assertions.assertEquals(test.getOrganisms(), result.getOrganisms());
    }
}
