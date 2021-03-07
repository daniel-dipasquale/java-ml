package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashMap;
import java.util.HashSet;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsConnectionGeneSupport {
    @Builder.Default
    private final boolean allowRecurrentConnections = true;
    @Builder.Default
    private final SequentialIdFactory innovationIdFactory = new SequentialIdFactoryLong();
    @Builder.Default
    private final SettingsFloatNumber weight = SettingsFloatNumber.randomGaussian(-2f, 2f);

    ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> createFactory() {
        return c -> {
            SequentialIdFactory sequentialIdFactory = new SequentialIdFactoryDefault("innovation-id", innovationIdFactory);

            return new ContextDefaultConnectionGeneSupport(allowRecurrentConnections, sequentialIdFactory, weight::get, new HashMap<>(), new HashSet<>());
        };
    }
}
