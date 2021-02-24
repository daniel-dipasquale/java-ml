package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuralNetworkDefault<T extends Comparable<T>> implements NeuralNetwork {
    private final Context<T> context;
    private final Genome<T> genome;
    private final Map<DirectedEdge<T>, DirectedEdgePermission<T>> cyclesAllowed = new HashMap<>();

    private boolean isCycleAllowed(final ConnectionGene<T> connection) {
        return !context.connections().allowCyclicConnections()
                || cyclesAllowed.computeIfAbsent(connection.getInnovationId().getDirectedEdge(), de -> new DirectedEdgePermission<>(de, connection.getCyclesAllowed())).isCycleAllowed();
    }

    private void processInputNodes(final float[] input, final Map<NodeGene<T>, Neuron<T>> neurons, final HashedQueue<Neuron<T>> neuronsToVisit) {
        Iterable<NodeGene<T>> nodes = () -> genome.getNodes().iterator(NodeGene.Type.Input);
        int index = 0;

        for (NodeGene<T> node : nodes) {
            Neuron<T> neuron = neurons.computeIfAbsent(node, Neuron::new);

            neuron.setValue(input[index++]);

            for (ConnectionGene<T> connection : genome.getConnections().getOutgoingFromNodeFromExpressed(node).values()) {
                NodeGene<T> nodeToVisit = genome.getNodes().getById(connection.getInnovationId().getInNodeId());
                Neuron<T> neuronToVisit = neurons.computeIfAbsent(nodeToVisit, Neuron::new);

                isCycleAllowed(connection);
                neuronToVisit.addToValue(neuron.getValue());
                neuronsToVisit.push(neuronToVisit); // TODO: only allow activation when all inputs are provided
            }
        }
    }

    private void processHiddenNodes(final Map<NodeGene<T>, Neuron<T>> neurons, final HashedQueue<Neuron<T>> neuronsToVisit) {
        while (!neuronsToVisit.isEmpty()) {
            Neuron<T> neuron = neuronsToVisit.pop();

            for (ConnectionGene<T> connection : genome.getConnections().getOutgoingFromNodeFromExpressed(neuron.getNode()).values()) {
                if (isCycleAllowed(connection)) {
                    NodeGene<T> nodeToVisit = genome.getNodes().getById(connection.getInnovationId().getInNodeId());
                    Neuron<T> neuronToVisit = neurons.computeIfAbsent(nodeToVisit, Neuron::new);

                    neuronToVisit.addToValue(neuron.getValue() * connection.getWeight());
                    neuronsToVisit.push(neuronToVisit);
                }
            }
        }
    }

    private float[] processOutputNodes(final Map<NodeGene<T>, Neuron<T>> neurons) {
        float[] output = new float[genome.getNodes().size(NodeGene.Type.Output)];
        Iterable<NodeGene<T>> nodes = () -> genome.getNodes().iterator(NodeGene.Type.Output);
        int index = 0;

        for (NodeGene<T> node : nodes) {
            output[index++] = Optional.ofNullable(neurons.get(node))
                    .map(Neuron::getValue)
                    .orElse(0f);
        }

        return output;
    }

    @Override
    public float[] activate(final float[] input) {
        cyclesAllowed.clear();

        Map<NodeGene<T>, Neuron<T>> neurons = new HashMap<>();
        HashedQueue<Neuron<T>> neuronsToVisit = new HashedQueue<>();

        processInputNodes(input, neurons, neuronsToVisit);
        processHiddenNodes(neurons, neuronsToVisit);

        return processOutputNodes(neurons);
    }

    @Override
    public void reset() {
    }
}
