package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.sequence.LongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.DefaultLongCounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public final class NeuronNavigatorTest {
    private static List<SequentialId> createNodeIds(final int count) {
        LongSequentialIdFactory nodeIdFactory = new LongSequentialIdFactory(new DefaultLongCounter(0L));

        return IntStream.range(0, count)
                .mapToObj(i -> nodeIdFactory.create())
                .collect(Collectors.toList());
    }

    private static List<NodeGene> createNodes(final List<Float> inputBiases, final List<Float> outputBiases, final List<Float> biasBiases, final List<Float> hiddenBiases) {
        List<SequentialId> ids = createNodeIds(inputBiases.size() + outputBiases.size() + biasBiases.size() + hiddenBiases.size());
        List<NodeGene> nodes = new ArrayList<>();

        IntStream.range(0, inputBiases.size())
                .mapToObj(i -> new NodeGene(ids.get(i), NodeGeneType.INPUT, inputBiases.get(i), IdentityActivationFunction.getInstance()))
                .forEach(nodes::add);

        IntStream.range(0, outputBiases.size())
                .mapToObj(i -> new NodeGene(ids.get(inputBiases.size() + i), NodeGeneType.OUTPUT, outputBiases.get(i), IdentityActivationFunction.getInstance()))
                .forEach(nodes::add);

        IntStream.range(0, biasBiases.size())
                .mapToObj(i -> new NodeGene(ids.get(inputBiases.size() + outputBiases.size() + i), NodeGeneType.BIAS, biasBiases.get(i), IdentityActivationFunction.getInstance()))
                .forEach(nodes::add);

        IntStream.range(0, hiddenBiases.size())
                .mapToObj(i -> new NodeGene(ids.get(inputBiases.size() + outputBiases.size() + biasBiases.size() + i), NodeGeneType.HIDDEN, hiddenBiases.get(i), IdentityActivationFunction.getInstance()))
                .forEach(nodes::add);

        return nodes;
    }

    /*
    [ 1 ] => [ 4, 5 ]
    [ 2 ] => [ 5 ]
    [ 3 ] => [ 4 ]
    [ 5 ] => [ 4 ]
    [ 4 ] => []
     */
    private static List<Neuron> createFeedForwardNetwork() {
        List<NodeGene> nodes = createNodes(List.of(0f, 1f, 2f), List.of(3f), List.of(), List.of(4f));

        return List.of(
                Neuron.builder()
                        .node(nodes.get(0))
                        .inputs(List.of())
                        .outputs(List.of(
                                new OutputConnection(nodes.get(3).getId(), 1f),
                                new OutputConnection(nodes.get(4).getId(), 1f)
                        ))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(1))
                        .inputs(List.of())
                        .outputs(List.of(new OutputConnection(nodes.get(4).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(2))
                        .inputs(List.of())
                        .outputs(List.of(new OutputConnection(nodes.get(3).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(3))
                        .inputs(List.of(
                                new InputConnection(nodes.get(0).getId(), 1),
                                new InputConnection(nodes.get(2).getId(), 1),
                                new InputConnection(nodes.get(4).getId(), 1)
                        ))
                        .outputs(List.of())
                        .build(),
                Neuron.builder()
                        .node(nodes.get(4))
                        .inputs(List.of(
                                new InputConnection(nodes.get(0).getId(), 1),
                                new InputConnection(nodes.get(1).getId(), 1)
                        ))
                        .outputs(List.of(new OutputConnection(nodes.get(3).getId(), 1f)))
                        .build()
        );
    }

    /*
    [ 1 ] => [ 4, 5 ]
    [ 2 ] => [ 5 ]
    [ 3 ] => [ 4 ]
    [ 5 ] => [ 4, 5 ]
    [ 4 ] => []
     */
    private static List<Neuron> createRecurrentReflexiveNetwork() {
        List<NodeGene> nodes = createNodes(List.of(0f, 1f, 2f), List.of(3f), List.of(), List.of(4f));

        return List.of(
                Neuron.builder()
                        .node(nodes.get(0))
                        .inputs(List.of())
                        .outputs(List.of(
                                new OutputConnection(nodes.get(3).getId(), 1f),
                                new OutputConnection(nodes.get(4).getId(), 1f)
                        ))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(1))
                        .inputs(List.of())
                        .outputs(List.of(new OutputConnection(nodes.get(4).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(2))
                        .inputs(List.of())
                        .outputs(List.of(new OutputConnection(nodes.get(3).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(3))
                        .inputs(List.of(
                                new InputConnection(nodes.get(0).getId(), 1),
                                new InputConnection(nodes.get(2).getId(), 1),
                                new InputConnection(nodes.get(4).getId(), 1)
                        ))
                        .outputs(List.of())
                        .build(),
                Neuron.builder()
                        .node(nodes.get(4))
                        .inputs(List.of(
                                new InputConnection(nodes.get(0).getId(), 1),
                                new InputConnection(nodes.get(1).getId(), 1),
                                new InputConnection(nodes.get(4).getId(), 1) // recurrent
                        ))
                        .outputs(List.of(
                                new OutputConnection(nodes.get(3).getId(), 1f),
                                new OutputConnection(nodes.get(4).getId(), 1f)
                        ))
                        .build()
        );
    }

    /*
    [ 1 ] => [ 4, 5 ]
    [ 2 ] => [ 5 ]
    [ 3 ] => [ 4 ]
    [ 5 ] => [ 4 ]
    [ 4 ] => [ 5 ]
     */
    private static List<Neuron> createRecurrentSymmetricNetwork() {
        List<NodeGene> nodes = createNodes(List.of(0f, 1f, 2f), List.of(3f), List.of(), List.of(4f));

        return List.of(
                Neuron.builder()
                        .node(nodes.get(0))
                        .inputs(List.of())
                        .outputs(List.of(
                                new OutputConnection(nodes.get(3).getId(), 1f),
                                new OutputConnection(nodes.get(4).getId(), 1f)
                        ))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(1))
                        .inputs(List.of())
                        .outputs(List.of(new OutputConnection(nodes.get(4).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(2))
                        .inputs(List.of())
                        .outputs(List.of(new OutputConnection(nodes.get(3).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(3))
                        .inputs(List.of(
                                new InputConnection(nodes.get(0).getId(), 1),
                                new InputConnection(nodes.get(2).getId(), 1),
                                new InputConnection(nodes.get(4).getId(), 1)
                        ))
                        .outputs(List.of(new OutputConnection(nodes.get(4).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(4))
                        .inputs(List.of(
                                new InputConnection(nodes.get(0).getId(), 1),
                                new InputConnection(nodes.get(1).getId(), 1),
                                new InputConnection(nodes.get(3).getId(), 1) // recurrent
                        ))
                        .outputs(List.of(new OutputConnection(nodes.get(3).getId(), 1f)))
                        .build()
        );
    }

    /*
    [ 1 ] => [ 4 ]
    [ 2 ] => [ 5 ]
    [ 4 ] => [ 3 ]
    [ 5 ] => [ 4 ]
    [ 3 ] => [ 5 ]
     */
    private static List<Neuron> createRecurrentTransitiveNetwork() {
        List<NodeGene> nodes = createNodes(List.of(0f, 1f), List.of(3f), List.of(), List.of(4f, 5f));

        return List.of(
                Neuron.builder()
                        .node(nodes.get(0))
                        .inputs(List.of())
                        .outputs(List.of(new OutputConnection(nodes.get(3).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(1))
                        .inputs(List.of())
                        .outputs(List.of(new OutputConnection(nodes.get(4).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(2))
                        .inputs(List.of(new InputConnection(nodes.get(3).getId(), 1)))
                        .outputs(List.of(new OutputConnection(nodes.get(4).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(3))
                        .inputs(List.of(
                                new InputConnection(nodes.get(0).getId(), 1),
                                new InputConnection(nodes.get(4).getId(), 1)
                        ))
                        .outputs(List.of(new OutputConnection(nodes.get(2).getId(), 1f)))
                        .build(),
                Neuron.builder()
                        .node(nodes.get(4))
                        .inputs(List.of(
                                new InputConnection(nodes.get(1).getId(), 1),
                                new InputConnection(nodes.get(2).getId(), 1) // recursive
                        ))
                        .outputs(List.of(new OutputConnection(nodes.get(3).getId(), 1f)))
                        .build()
        );
    }

    @Test
    public void TEST_feed_forward_with_feed_forward_path_builder() {
        List<Neuron> neurons = createFeedForwardNetwork();
        NeuronNavigator test = new NeuronNavigator(new FeedForwardNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(
                neurons.get(1).getId(),
                neurons.get(0).getId(),
                neurons.get(4).getId(),
                neurons.get(2).getId()
        ), result);
    }

    @Test
    public void TEST_recurrent_reflexive_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentReflexiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new FeedForwardNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(
                neurons.get(1).getId(),
                neurons.get(0).getId(),
                neurons.get(4).getId(),
                neurons.get(2).getId()
        ), result);
    }

    @Test
    public void TEST_recurrent_symmetric_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentSymmetricNetwork();
        NeuronNavigator test = new NeuronNavigator(new FeedForwardNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(
                neurons.get(1).getId(),
                neurons.get(0).getId(),
                neurons.get(4).getId(),
                neurons.get(2).getId()
        ), result);
    }

    @Test
    public void TEST_recurrent_transitive_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentTransitiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new FeedForwardNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(
                neurons.get(1).getId(),
                neurons.get(4).getId(),
                neurons.get(0).getId(),
                neurons.get(3).getId()
        ), result);
    }

    @Test
    public void TEST_feed_forward_with_recurrent_path_builder() {
        List<Neuron> neurons = createFeedForwardNetwork();
        NeuronNavigator test = new NeuronNavigator(new RecurrentNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(
                neurons.get(1).getId(),
                neurons.get(0).getId(),
                neurons.get(4).getId(),
                neurons.get(2).getId()
        ), result);
    }

    @Test
    public void TEST_recurrent_reflexive_with_recurrent_path_builder() {
        List<Neuron> neurons = createRecurrentReflexiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new RecurrentNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(
                neurons.get(4).getId(),
                neurons.get(1).getId(),
                neurons.get(0).getId(),
                neurons.get(4).getId(),
                neurons.get(2).getId()
        ), result);
    }

    @Test
    public void TEST_recurrent_symmetric_with_recurrent_path_builder() {
        List<Neuron> neurons = createRecurrentSymmetricNetwork();
        NeuronNavigator test = new NeuronNavigator(new RecurrentNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(
                neurons.get(3).getId(),
                neurons.get(1).getId(),
                neurons.get(0).getId(),
                neurons.get(4).getId(),
                neurons.get(2).getId()
        ), result);
    }

    @Test
    public void TEST_recurrent_transitive_with_recurrent_path_builder() {
        List<Neuron> neurons = createRecurrentTransitiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new RecurrentNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(
                neurons.get(2).getId(),
                neurons.get(1).getId(),
                neurons.get(4).getId(),
                neurons.get(0).getId(),
                neurons.get(3).getId()
        ), result);
    }
}
