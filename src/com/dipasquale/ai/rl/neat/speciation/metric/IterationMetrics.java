package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public final class IterationMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = 1590234190652192689L;
    private final Map<Integer, GenerationMetrics> generations;

    public IterationMetrics createCopy(final MapFactory mapFactory) {
        Map<Integer, GenerationMetrics> generationsCopied = generations.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createCopy(mapFactory)));

        return new IterationMetrics(mapFactory.create(generationsCopied));
    }

    public void clear() {
        generations.clear();
    }
}
