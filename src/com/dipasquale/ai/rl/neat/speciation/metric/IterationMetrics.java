package com.dipasquale.ai.rl.neat.speciation.metric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public final class IterationMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = 1590234190652192689L;
    private final Map<Integer, GenerationMetrics> generations;

    public IterationMetrics() {
        this(new HashMap<>());
    }

    public IterationMetrics createCopy() {
        Map<Integer, GenerationMetrics> copiedGenerations = generations.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createCopy()));

        return new IterationMetrics(new HashMap<>(copiedGenerations));
    }

    public void clear() {
        generations.clear();
    }
}
