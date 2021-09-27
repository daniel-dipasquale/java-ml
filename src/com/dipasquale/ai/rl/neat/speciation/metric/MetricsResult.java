package com.dipasquale.ai.rl.neat.speciation.metric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class MetricsResult {
    private final String defaultKey;
    private final List<MetricsRecord> records;
}
