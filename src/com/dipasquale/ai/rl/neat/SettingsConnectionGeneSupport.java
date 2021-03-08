package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class SettingsConnectionGeneSupport {
    @Builder.Default
    private final boolean recurrentConnectionsAllowed = true;
    @Builder.Default
    private final SequentialIdFactory innovationIdFactory = new SequentialIdFactoryLong();
    @Builder.Default
    private final SettingsFloatNumber weight = SettingsFloatNumber.randomGaussian(-2f, 2f);

    ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> createFactory() {
        return c -> {
            SequentialIdFactory sequentialIdFactory = new SequentialIdFactoryDefault("innovation-id", innovationIdFactory);

            return new ContextDefaultConnectionGeneSupport(recurrentConnectionsAllowed, sequentialIdFactory, weight::get, new ConcurrentHashMap<>(), Collections.newSetFromMap(new ConcurrentHashMap<>()));
        };
    }
}
