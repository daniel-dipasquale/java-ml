package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.common.Id;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public final class DualModeNodeGeneIdFactory implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -361628531407045333L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final Map<NodeGeneType, DualModeIdFactory> nodeIdFactories;

    public DualModeNodeGeneIdFactory(final int concurrencyLevel) {
        this.concurrencyLevelState = new ConcurrencyLevelState(concurrencyLevel);
        this.nodeIdFactories = createNodeIdFactories(concurrencyLevel);
    }

    private static Map<NodeGeneType, DualModeIdFactory> createNodeIdFactories(final int concurrencyLevel) {
        EnumMap<NodeGeneType, DualModeIdFactory> nodeIdFactories = new EnumMap<>(NodeGeneType.class);

        nodeIdFactories.put(NodeGeneType.INPUT, new DualModeIdFactory(concurrencyLevel, "n1-input"));
        nodeIdFactories.put(NodeGeneType.OUTPUT, new DualModeIdFactory(concurrencyLevel, "n4-output"));
        nodeIdFactories.put(NodeGeneType.BIAS, new DualModeIdFactory(concurrencyLevel, "n2-bias"));
        nodeIdFactories.put(NodeGeneType.HIDDEN, new DualModeIdFactory(concurrencyLevel, "n3-hidden"));

        return nodeIdFactories;
    }

    public Id createNodeId(final NodeGeneType type) {
        return nodeIdFactories.get(type).create();
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);

        for (DualModeIdFactory sequentialIdFactory : nodeIdFactories.values()) {
            sequentialIdFactory.activateMode(concurrencyLevel);
        }
    }

    public void reset() {
        for (DualModeIdFactory sequentialIdFactory : nodeIdFactories.values()) {
            sequentialIdFactory.reset();
        }
    }
}
