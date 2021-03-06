package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashMap;
import java.util.HashSet;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsConnectionGeneSupport<T extends Comparable<T>> {
    @Builder.Default
    private final boolean allowRecurrentConnections = true;
    private final SequentialIdFactory<T> innovationIdFactory;
    @Builder.Default
    private final SettingsFloatNumber weight = SettingsFloatNumber.randomGaussian(-2f, 2f);

    ContextDefaultComponentFactory<T, ContextDefaultConnectionGeneSupport<T>> createFactory() {
        return c -> new ContextDefaultConnectionGeneSupport<>(allowRecurrentConnections, innovationIdFactory, weight::get, new HashMap<>(), new HashSet<>());
    }
}
