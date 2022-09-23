package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.ai.rl.neat.IdFactory;
import com.dipasquale.ai.rl.neat.IdType;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
public final class NodeGeneIdFactory implements Serializable {
    @Serial
    private static final long serialVersionUID = -361628531407045333L;
    private final Map<NodeGeneType, IdFactory> idFactories = createIdFactories();

    private static Map<NodeGeneType, IdFactory> createIdFactories() {
        EnumMap<NodeGeneType, IdFactory> idFactories = new EnumMap<>(NodeGeneType.class);

        idFactories.put(NodeGeneType.INPUT, new IdFactory(IdType.INPUT_NODE));
        idFactories.put(NodeGeneType.OUTPUT, new IdFactory(IdType.OUTPUT_NODE));
        idFactories.put(NodeGeneType.BIAS, new IdFactory(IdType.BIAS_NODE));
        idFactories.put(NodeGeneType.HIDDEN, new IdFactory(IdType.HIDDEN_NODE));

        return idFactories;
    }

    public Id create(final NodeGeneType type) {
        return idFactories.get(type).create();
    }

    public void reset() {
        for (IdFactory idFactory : idFactories.values()) {
            idFactory.reset();
        }
    }
}
