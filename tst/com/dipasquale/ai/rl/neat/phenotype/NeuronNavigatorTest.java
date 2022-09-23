package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.LongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.NamedLongSequentialId;
import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.ai.rl.neat.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.StandardLongValue;
import com.dipasquale.data.structure.collection.ListSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public final class NeuronNavigatorTest {
    private static List<Id> createNodeIds(final int count) {
        LongSequentialIdFactory nodeIdFactory = new LongSequentialIdFactory(new StandardLongValue(0L));

        return IntStream.range(0, count)
                .mapToObj(__ -> new Id(new NamedLongSequentialId("test", nodeIdFactory.create())))
                .collect(Collectors.toList());
    }

    private static List<NodeGene> createNodeGenes(final List<Float> inputBiases, final List<Float> outputBiases, final List<Float> biasBiases, final List<Float> hiddenBiases) {
        List<Id> ids = createNodeIds(inputBiases.size() + outputBiases.size() + biasBiases.size() + hiddenBiases.size());
        List<NodeGene> nodeGenes = new ArrayList<>();

        IntStream.range(0, inputBiases.size())
                .mapToObj(index -> new NodeGene(ids.get(index), NodeGeneType.INPUT, inputBiases.get(index), List.of(), IdentityActivationFunction.getInstance()))
                .forEach(nodeGenes::add);

        IntStream.range(0, outputBiases.size())
                .mapToObj(index -> new NodeGene(ids.get(inputBiases.size() + index), NodeGeneType.OUTPUT, outputBiases.get(index), List.of(), IdentityActivationFunction.getInstance()))
                .forEach(nodeGenes::add);

        IntStream.range(0, biasBiases.size())
                .mapToObj(index -> new NodeGene(ids.get(inputBiases.size() + outputBiases.size() + index), NodeGeneType.BIAS, biasBiases.get(index), List.of(), IdentityActivationFunction.getInstance()))
                .forEach(nodeGenes::add);

        IntStream.range(0, hiddenBiases.size())
                .mapToObj(index -> new NodeGene(ids.get(inputBiases.size() + outputBiases.size() + biasBiases.size() + index), NodeGeneType.HIDDEN, hiddenBiases.get(index), List.of(), IdentityActivationFunction.getInstance()))
                .forEach(nodeGenes::add);

        return nodeGenes;
    }

    /*
    [ 1 ] => [ 4, 5 ]
    [ 2 ] => [ 5 ]
    [ 3 ] => [ 4 ]
    [ 5 ] => [ 4 ]
    [ 4 ] => []
     */
    private static List<Neuron> createFeedForwardNetwork() {
        List<NodeGene> nodeGenes = createNodeGenes(List.of(0f, 1f, 2f), List.of(3f), List.of(), List.of(4f));

        return ListSupport.<Neuron>builder()
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(0))
                        .inputConnections(List.of())
                        .outputConnections(List.of(
                                new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of()),
                                new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())
                        ))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(1))
                        .inputConnections(List.of())
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(2))
                        .inputConnections(List.of())
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(3))
                        .inputConnections(List.of(
                                new NeuronInputConnection(nodeGenes.get(0).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(2).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(4).getId(), 1)
                        ))
                        .outputConnections(List.of())
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(4))
                        .inputConnections(List.of(
                                new NeuronInputConnection(nodeGenes.get(0).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(1).getId(), 1)
                        ))
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of())))
                        .build())
                .build();
    }

    /*
    [ 1 ] => [ 4, 5 ]
    [ 2 ] => [ 5 ]
    [ 3 ] => [ 4 ]
    [ 5 ] => [ 4, 5 ]
    [ 4 ] => []
     */
    private static List<Neuron> createRecurrentReflexiveNetwork() {
        List<NodeGene> nodeGenes = createNodeGenes(List.of(0f, 1f, 2f), List.of(3f), List.of(), List.of(4f));

        return ListSupport.<Neuron>builder()
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(0))
                        .inputConnections(List.of())
                        .outputConnections(List.of(
                                new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of()),
                                new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())
                        ))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(1))
                        .inputConnections(List.of())
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(2))
                        .inputConnections(List.of())
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(3))
                        .inputConnections(List.of(
                                new NeuronInputConnection(nodeGenes.get(0).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(2).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(4).getId(), 1)
                        ))
                        .outputConnections(List.of())
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(4))
                        .inputConnections(List.of(
                                new NeuronInputConnection(nodeGenes.get(0).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(1).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(4).getId(), 1) // recurrent
                        ))
                        .outputConnections(List.of(
                                new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of()),
                                new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())
                        ))
                        .build())
                .build();
    }

    /*
    [ 1 ] => [ 4, 5 ]
    [ 2 ] => [ 5 ]
    [ 3 ] => [ 4 ]
    [ 5 ] => [ 4 ]
    [ 4 ] => [ 5 ]
     */
    private static List<Neuron> createRecurrentSymmetricNetwork() {
        List<NodeGene> nodeGenes = createNodeGenes(List.of(0f, 1f, 2f), List.of(3f), List.of(), List.of(4f));

        return ListSupport.<Neuron>builder()
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(0))
                        .inputConnections(List.of())
                        .outputConnections(List.of(
                                new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of()),
                                new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())
                        ))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(1))
                        .inputConnections(List.of())
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(2))
                        .inputConnections(List.of())
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(3))
                        .inputConnections(List.of(
                                new NeuronInputConnection(nodeGenes.get(0).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(2).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(4).getId(), 1)
                        ))
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(4))
                        .inputConnections(List.of(
                                new NeuronInputConnection(nodeGenes.get(0).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(1).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(3).getId(), 1) // recurrent
                        ))
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of())))
                        .build())
                .build();
    }

    /*
    [ 1 ] => [ 4 ]
    [ 2 ] => [ 5 ]
    [ 4 ] => [ 3 ]
    [ 5 ] => [ 4 ]
    [ 3 ] => [ 5 ]
     */
    private static List<Neuron> createRecurrentTransitiveNetwork() {
        List<NodeGene> nodeGenes = createNodeGenes(List.of(0f, 1f), List.of(3f), List.of(), List.of(4f, 5f));

        return ListSupport.<Neuron>builder()
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(0))
                        .inputConnections(List.of())
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(1))
                        .inputConnections(List.of())
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(2))
                        .inputConnections(List.of(new NeuronInputConnection(nodeGenes.get(3).getId(), 1)))
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(4).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(3))
                        .inputConnections(List.of(
                                new NeuronInputConnection(nodeGenes.get(0).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(4).getId(), 1)
                        ))
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(2).getId(), 1f, List.of())))
                        .build())
                .add(Neuron.builder()
                        .nodeGene(nodeGenes.get(4))
                        .inputConnections(List.of(
                                new NeuronInputConnection(nodeGenes.get(1).getId(), 1),
                                new NeuronInputConnection(nodeGenes.get(2).getId(), 1) // recursive
                        ))
                        .outputConnections(List.of(new NeuronOutputConnection(nodeGenes.get(3).getId(), 1f, List.of())))
                        .build())
                .build();
    }

    @Test
    public void TEST_feed_forward_with_feed_forward_path_builder() {
        List<Neuron> neurons = createFeedForwardNetwork();
        NeuronNavigator test = new NeuronNavigator(new AcyclicNeuronPathBuilder(), IdentityNeuronLayerTopologyDefinition.getInstance());

        neurons.forEach(test::add);
        test.build();

        List<Id> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ListSupport.<Id>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_reflexive_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentReflexiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new AcyclicNeuronPathBuilder(), IdentityNeuronLayerTopologyDefinition.getInstance());

        neurons.forEach(test::add);
        test.build();

        List<Id> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ListSupport.<Id>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_symmetric_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentSymmetricNetwork();
        NeuronNavigator test = new NeuronNavigator(new AcyclicNeuronPathBuilder(), IdentityNeuronLayerTopologyDefinition.getInstance());

        neurons.forEach(test::add);
        test.build();

        List<Id> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ListSupport.<Id>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_transitive_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentTransitiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new AcyclicNeuronPathBuilder(), IdentityNeuronLayerTopologyDefinition.getInstance());

        neurons.forEach(test::add);
        test.build();

        List<Id> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ListSupport.<Id>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(3).getId())
                .build(), result);
    }

    @Test
    public void TEST_feed_forward_with_recurrent_path_builder() {
        List<Neuron> neurons = createFeedForwardNetwork();
        NeuronNavigator test = new NeuronNavigator(new CyclicNeuronPathBuilder(), IdentityNeuronLayerTopologyDefinition.getInstance());

        neurons.forEach(test::add);
        test.build();

        List<Id> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ListSupport.<Id>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_reflexive_with_recurrent_path_builder() {
        List<Neuron> neurons = createRecurrentReflexiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new CyclicNeuronPathBuilder(), IdentityNeuronLayerTopologyDefinition.getInstance());

        neurons.forEach(test::add);
        test.build();

        List<Id> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ListSupport.<Id>builder()
                .add(neurons.get(4).getId())
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_symmetric_with_recurrent_path_builder() {
        List<Neuron> neurons = createRecurrentSymmetricNetwork();
        NeuronNavigator test = new NeuronNavigator(new CyclicNeuronPathBuilder(), IdentityNeuronLayerTopologyDefinition.getInstance());

        neurons.forEach(test::add);
        test.build();

        List<Id> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ListSupport.<Id>builder()
                .add(neurons.get(3).getId())
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_transitive_with_recurrent_path_builder() {
        List<Neuron> neurons = createRecurrentTransitiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new CyclicNeuronPathBuilder(), IdentityNeuronLayerTopologyDefinition.getInstance());

        neurons.forEach(test::add);
        test.build();

        List<Id> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ListSupport.<Id>builder()
                .add(neurons.get(2).getId())
                .add(neurons.get(1).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(3).getId())
                .build(), result);
    }
}
