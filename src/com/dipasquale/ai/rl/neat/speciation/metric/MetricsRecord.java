package com.dipasquale.ai.rl.neat.speciation.metric;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class MetricsRecord {
    private final Map<String, Float> values = new HashMap<>();

    public Float getValue(final String name) {
        return values.get(name);
    }

    public void setValue(final String name, final Float value) {
        values.put(name, value);
    }
}
