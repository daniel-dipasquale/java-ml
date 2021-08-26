package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.DualModeSequentialIdFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public final class DualModeNodeIdFactory implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -361628531407045333L;
    private final Map<NodeGeneType, DualModeSequentialIdFactory> nodeIdFactories;

    public DualModeNodeIdFactory(final boolean concurrent) {
        this.nodeIdFactories = createNodeIdFactories(concurrent);
    }

    private static Map<NodeGeneType, DualModeSequentialIdFactory> createNodeIdFactories(final boolean concurrent) {
        EnumMap<NodeGeneType, DualModeSequentialIdFactory> nodeIdFactories = new EnumMap<>(NodeGeneType.class);

        nodeIdFactories.put(NodeGeneType.INPUT, new DualModeSequentialIdFactory(concurrent, "n1-input"));
        nodeIdFactories.put(NodeGeneType.OUTPUT, new DualModeSequentialIdFactory(concurrent, "n4-output"));
        nodeIdFactories.put(NodeGeneType.BIAS, new DualModeSequentialIdFactory(concurrent, "n2-bias"));
        nodeIdFactories.put(NodeGeneType.HIDDEN, new DualModeSequentialIdFactory(concurrent, "n3-hidden"));

        return nodeIdFactories;
    }

    public SequentialId createNodeId(final NodeGeneType type) {
        return nodeIdFactories.get(type).create();
    }

    @Override
    public void switchMode(final boolean concurrent) {
        for (DualModeSequentialIdFactory sequentialIdFactory : nodeIdFactories.values()) {
            sequentialIdFactory.switchMode(concurrent);
        }
    }
}
