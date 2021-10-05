package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.sequence.LongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.DefaultLongCounter;
import com.google.common.collect.ImmutableList;
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
        List<NodeGene> nodes = createNodes(ImmutableList.of(0f, 1f, 2f), ImmutableList.of(3f), ImmutableList.of(), ImmutableList.of(4f));

        return ImmutableList.<Neuron>builder()
                .add(Neuron.builder()
                        .node(nodes.get(0))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(1))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(2))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(3))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(0).getId(), 1))
                                .add(new InputConnection(nodes.get(2).getId(), 1))
                                .add(new InputConnection(nodes.get(4).getId(), 1))
                                .build())
                        .outputs(ImmutableList.of())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(4))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(0).getId(), 1))
                                .add(new InputConnection(nodes.get(1).getId(), 1))
                                .build())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .build())
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
        List<NodeGene> nodes = createNodes(ImmutableList.of(0f, 1f, 2f), ImmutableList.of(3f), ImmutableList.of(), ImmutableList.of(4f));

        return ImmutableList.<Neuron>builder()
                .add(Neuron.builder()
                        .node(nodes.get(0))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(1))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(2))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(3))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(0).getId(), 1))
                                .add(new InputConnection(nodes.get(2).getId(), 1))
                                .add(new InputConnection(nodes.get(4).getId(), 1))
                                .build())
                        .outputs(ImmutableList.of())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(4))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(0).getId(), 1))
                                .add(new InputConnection(nodes.get(1).getId(), 1))
                                .add(new InputConnection(nodes.get(4).getId(), 1)) // recurrent
                                .build())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
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
        List<NodeGene> nodes = createNodes(ImmutableList.of(0f, 1f, 2f), ImmutableList.of(3f), ImmutableList.of(), ImmutableList.of(4f));

        return ImmutableList.<Neuron>builder()
                .add(Neuron.builder()
                        .node(nodes.get(0))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(1))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(2))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(3))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(0).getId(), 1))
                                .add(new InputConnection(nodes.get(2).getId(), 1))
                                .add(new InputConnection(nodes.get(4).getId(), 1))
                                .build())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(4))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(0).getId(), 1))
                                .add(new InputConnection(nodes.get(1).getId(), 1))
                                .add(new InputConnection(nodes.get(3).getId(), 1)) // recurrent
                                .build())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .build())
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
        List<NodeGene> nodes = createNodes(ImmutableList.of(0f, 1f), ImmutableList.of(3f), ImmutableList.of(), ImmutableList.of(4f, 5f));

        return ImmutableList.<Neuron>builder()
                .add(Neuron.builder()
                        .node(nodes.get(0))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(1))
                        .inputs(ImmutableList.of())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(2))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(3).getId(), 1))
                                .build())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(4).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(3))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(0).getId(), 1))
                                .add(new InputConnection(nodes.get(4).getId(), 1))
                                .build())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(2).getId(), 1f))
                                .build())
                        .build())
                .add(Neuron.builder()
                        .node(nodes.get(4))
                        .inputs(ImmutableList.<InputConnection>builder()
                                .add(new InputConnection(nodes.get(1).getId(), 1))
                                .add(new InputConnection(nodes.get(2).getId(), 1)) // recursive
                                .build())
                        .outputs(ImmutableList.<OutputConnection>builder()
                                .add(new OutputConnection(nodes.get(3).getId(), 1f))
                                .build())
                        .build())
                .build();
    }

    @Test
    public void TEST_feed_forward_with_feed_forward_path_builder() {
        List<Neuron> neurons = createFeedForwardNetwork();
        NeuronNavigator test = new NeuronNavigator(new FeedForwardNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ImmutableList.<SequentialId>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_reflexive_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentReflexiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new FeedForwardNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ImmutableList.<SequentialId>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_symmetric_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentSymmetricNetwork();
        NeuronNavigator test = new NeuronNavigator(new FeedForwardNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ImmutableList.<SequentialId>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_transitive_with_feed_forward_path_builder() {
        List<Neuron> neurons = createRecurrentTransitiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new FeedForwardNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ImmutableList.<SequentialId>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(3).getId())
                .build(), result);
    }

    @Test
    public void TEST_feed_forward_with_recurrent_path_builder() {
        List<Neuron> neurons = createFeedForwardNetwork();
        NeuronNavigator test = new NeuronNavigator(new RecurrentNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ImmutableList.<SequentialId>builder()
                .add(neurons.get(1).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(2).getId())
                .build(), result);
    }

    @Test
    public void TEST_recurrent_reflexive_with_recurrent_path_builder() {
        List<Neuron> neurons = createRecurrentReflexiveNetwork();
        NeuronNavigator test = new NeuronNavigator(new RecurrentNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ImmutableList.<SequentialId>builder()
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
        NeuronNavigator test = new NeuronNavigator(new RecurrentNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ImmutableList.<SequentialId>builder()
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
        NeuronNavigator test = new NeuronNavigator(new RecurrentNeuronPathBuilder());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(ImmutableList.<SequentialId>builder()
                .add(neurons.get(2).getId())
                .add(neurons.get(1).getId())
                .add(neurons.get(4).getId())
                .add(neurons.get(0).getId())
                .add(neurons.get(3).getId())
                .build(), result);
    }
}
