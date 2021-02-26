//package com.dipasquale.ai.rl.neat;
//
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
//final class NeuralNetworkLayered<T extends Comparable<T>> implements NeuralNetwork {
//    private final Context<T> context;
//    private final NodeGeneMap<T> nodes;
//    private final ConnectionGeneMap<T> connections;
//    private final Map<Integer, Layer> layers = new HashMap<>();
//    private final Map<DirectedEdge<T>, Counter> connectionsCyclesAllowed = new HashMap<>();
//
//    private Layer createInitialLayer(final SequentialMap<T, NodeGene<T>> nodes) {
//        List<NeuralNetworkLayered<T>.NodeConnectionEntry> inputEntries = StreamSupport.stream(nodes.spliterator(), false)
//                .map(n -> new NeuralNetworkLayered<T>.NodeConnectionEntry(n, Arrays.asList(context.connections().createDirectedEdge(n, n))))
//                .collect(Collectors.toList());
//
//        List<NodeConnectionEntry> outputEntries = StreamSupport.stream(nodes.spliterator(), false)
//                .map(n -> new NodeConnectionEntry(n, connections.getOutgoingFromNodeIdFromExpressed(n.getId()).entrySet().stream()
//                        .filter(ce -> isCirculationAllowed(ce.getKey(), ce.getValue()))
//                        .map(Map.Entry::getValue)
//                        .map(ConnectionGene::getInnovationId)
//                        .map(InnovationId::getDirectedEdge)
//                        .collect(Collectors.toList())))
//                .collect(Collectors.toList());
//
//        float[] output = new float[outputEntries.stream()
//                .map(nce -> nce.directedEdges.size())
//                .reduce(0, Integer::sum)];
//
//        return new Layer(0, inputEntries, outputEntries, output);
//    }
//
//    private Layer createLayer(final int index, final Layer previousLayer) {
//        float[] output = new float[1];
//
//        return new Layer(index, previousLayer.outputEntries, null, output);
//    }
//
//    public float[] activate(final float[] input) {
//        Layer layer = layers.computeIfAbsent(0, i -> createInitialLayer(nodes.revealByType(NodeGene.Type.Input)));
//        float[] values = layer.forward(input);
//
//        connectionsCyclesAllowed.clear();
//
//        return null;
//    }
//
//    private boolean isCirculationAllowed(final DirectedEdge<T> innovationId, final ConnectionGene<T> connection) {
//        return connectionsCyclesAllowed.computeIfAbsent(innovationId, k -> new Counter(connection.getCyclesAllowed())).value-- > 0;
//    }
//
//    public void clear() {
//        layers.clear();
//    }
//
//    @AllArgsConstructor(access = AccessLevel.PACKAGE)
//    private static final class Counter {
//        private int value;
//    }
//
//    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
//    private final class NodeConnectionEntry {
//        private final NodeGene<T> node;
//        private final List<DirectedEdge<T>> directedEdges;
//    }
//
//    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
//    private final class Layer {
//        private final int index;
//        private final List<NodeConnectionEntry> inputEntries;
//        private final List<NodeConnectionEntry> outputEntries;
//        private final float[] output;
//
//        public float[] forward(final float[] input) {
//            for (int in = 0, cn = inputEntries.size(), ii = 0, io = 0; in < cn; in++) {
//                NodeConnectionEntry inputEntry = inputEntries.get(in);
//                float value = 0f;
//
//                if (index > 0) {
//                    Map<DirectedEdge<T>, ConnectionGene<T>> incomingConnections = connections.getIncomingToNodeIdFromExpressed(inputEntry.node.getId());
//
//                    for (Map.Entry<DirectedEdge<T>, ConnectionGene<T>> entry : incomingConnections.entrySet()) {
//                        value += entry.getValue().getWeight() * input[ii++];
//                    }
//
//                    value = inputEntry.node.getActivationFunction().forward(value + inputEntry.node.getBias());
//                } else {
//                    value = inputEntry.node.getActivationFunction().forward(input[ii++]);
//                }
//
//                Map<DirectedEdge<T>, ConnectionGene<T>> outgoingConnections = connections.getOutgoingFromNodeIdFromExpressed(inputEntry.node.getId());
//
//                for (Map.Entry<DirectedEdge<T>, ConnectionGene<T>> entry : outgoingConnections.entrySet()) {
//                    output[io++] = value;
//                }
//            }
//
//            return output;
//        }
//    }
//}
