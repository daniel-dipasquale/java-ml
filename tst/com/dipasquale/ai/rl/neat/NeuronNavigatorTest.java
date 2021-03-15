package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.common.CircularVersionInt;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class NeuronNavigatorTest {
    @Test
    public void TEST_1_feed_forward() {
        SequentialIdFactoryLong nodeIdFactory = new SequentialIdFactoryLong();
        CircularVersionInt activationNumber = new CircularVersionInt(0, 1);

        List<SequentialId> ids = ImmutableList.<SequentialId>builder()
                .add(nodeIdFactory.next())
                .add(nodeIdFactory.next())
                .add(nodeIdFactory.next())
                .add(nodeIdFactory.next())
                .add(nodeIdFactory.next())
                .build();

        List<NodeGene> nodes = ImmutableList.<NodeGene>builder()
                .add(new NodeGene(ids.get(0), NodeGeneType.Input, 0f, ActivationFunction.Identity))
                .add(new NodeGene(ids.get(1), NodeGeneType.Input, 1f, ActivationFunction.Identity))
                .add(new NodeGene(ids.get(2), NodeGeneType.Input, 2f, ActivationFunction.Identity))
                .add(new NodeGene(ids.get(3), NodeGeneType.Output, 3f, ActivationFunction.Identity))
                .add(new NodeGene(ids.get(4), NodeGeneType.Hidden, 4f, ActivationFunction.Identity))
                .build();

        List<Neuron> neurons = ImmutableList.<Neuron>builder()
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(0))
                        .inputIds(ImmutableSet.of())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(3), 1f, 0))
                                .add(new NeuronOutput(ids.get(4), 1f, 0))
                                .build())
                        .build())
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(1))
                        .inputIds(ImmutableSet.of())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(4), 1f, 0))
                                .build())
                        .build())
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(2))
                        .inputIds(ImmutableSet.of())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(3), 1f, 0))
                                .build())
                        .build())
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(3))
                        .inputIds(ImmutableSet.<SequentialId>builder()
                                .add(ids.get(0))
                                .add(ids.get(2))
                                .add(ids.get(4))
                                .build())
                        .outputs(ImmutableList.of())
                        .build())
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(4))
                        .inputIds(ImmutableSet.<SequentialId>builder()
                                .add(ids.get(0))
                                .add(ids.get(1))
                                .build())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(3), 1f, 0))
                                .build())
                        .build())
                .build();

        NeuronNavigator test = new NeuronNavigator(new NeuronPathBuilderDefault());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assert.assertEquals(ImmutableList.<SequentialId>builder()
                .add(ids.get(1))
                .add(ids.get(0))
                .add(ids.get(4))
                .add(ids.get(2))
                .add(ids.get(3))
                .build(), result);
    }

    @Test
    public void TEST_3_recurrent_symmetric() {
        SequentialIdFactoryLong nodeIdFactory = new SequentialIdFactoryLong();
        CircularVersionInt activationNumber = new CircularVersionInt(0, 1);

        List<SequentialId> ids = ImmutableList.<SequentialId>builder()
                .add(nodeIdFactory.next())
                .add(nodeIdFactory.next())
                .add(nodeIdFactory.next())
                .add(nodeIdFactory.next())
                .add(nodeIdFactory.next())
                .build();

        List<NodeGene> nodes = ImmutableList.<NodeGene>builder()
                .add(new NodeGene(ids.get(0), NodeGeneType.Input, 0f, ActivationFunction.Identity))
                .add(new NodeGene(ids.get(1), NodeGeneType.Input, 1f, ActivationFunction.Identity))
                .add(new NodeGene(ids.get(2), NodeGeneType.Input, 2f, ActivationFunction.Identity))
                .add(new NodeGene(ids.get(3), NodeGeneType.Output, 3f, ActivationFunction.Identity))
                .add(new NodeGene(ids.get(4), NodeGeneType.Hidden, 4f, ActivationFunction.Identity))
                .build();

        List<Neuron> neurons = ImmutableList.<Neuron>builder()
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(0))
                        .inputIds(ImmutableSet.of())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(3), 1f, 0))
                                .add(new NeuronOutput(ids.get(4), 1f, 0))
                                .build())
                        .build())
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(1))
                        .inputIds(ImmutableSet.of())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(4), 1f, 0))
                                .build())
                        .build())
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(2))
                        .inputIds(ImmutableSet.of())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(3), 1f, 0))
                                .build())
                        .build())
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(3))
                        .inputIds(ImmutableSet.<SequentialId>builder()
                                .add(ids.get(0))
                                .add(ids.get(2))
                                .add(ids.get(4))
                                .build())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(4), 1f, 0))
                                .build())
                        .build())
                .add(NeuronDefault.builder()
                        .activationNumber(activationNumber)
                        .node(nodes.get(4))
                        .inputIds(ImmutableSet.<SequentialId>builder()
                                .add(ids.get(0))
                                .add(ids.get(1))
                                .add(ids.get(3))
                                .build())
                        .outputs(ImmutableList.<NeuronOutput>builder()
                                .add(new NeuronOutput(ids.get(3), 1f, 0))
                                .build())
                        .build())
                .build();

        NeuronNavigator test = new NeuronNavigator(new NeuronPathBuilderDefault());

        neurons.forEach(test::add);

        List<SequentialId> result = StreamSupport.stream(test.spliterator(), false)
                .map(Neuron::getId)
                .collect(Collectors.toList());

        Assert.assertEquals(ImmutableList.<SequentialId>builder()
                .add(ids.get(1))
                .add(ids.get(0))
                .add(ids.get(4))
                .add(ids.get(2))
                .add(ids.get(3))
                .build(), result);
    }
}
