package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.DualModeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.IdType;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public final class DualModeNodeGeneIdFactory implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -361628531407045333L;
    private final Map<NodeGeneType, DualModeIdFactory> nodeIdFactories;

    public DualModeNodeGeneIdFactory(final int concurrencyLevel) {
        this.nodeIdFactories = createNodeIdFactories(concurrencyLevel);
    }

    private static Map<NodeGeneType, DualModeIdFactory> createNodeIdFactories(final int concurrencyLevel) {
        EnumMap<NodeGeneType, DualModeIdFactory> nodeIdFactories = new EnumMap<>(NodeGeneType.class);

        nodeIdFactories.put(NodeGeneType.INPUT, new DualModeIdFactory(concurrencyLevel, IdType.INPUT_NODE));
        nodeIdFactories.put(NodeGeneType.OUTPUT, new DualModeIdFactory(concurrencyLevel, IdType.OUTPUT_NODE));
        nodeIdFactories.put(NodeGeneType.BIAS, new DualModeIdFactory(concurrencyLevel, IdType.BIAS_NODE));
        nodeIdFactories.put(NodeGeneType.HIDDEN, new DualModeIdFactory(concurrencyLevel, IdType.HIDDEN_NODE));

        return nodeIdFactories;
    }

    public Id createNodeId(final NodeGeneType type) {
        return nodeIdFactories.get(type).create();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        DualModeObject.forEachValueActivateMode(nodeIdFactories, concurrencyLevel);
    }

    public void reset() {
        for (DualModeIdFactory sequentialIdFactory : nodeIdFactories.values()) {
            sequentialIdFactory.reset();
        }
    }
}
