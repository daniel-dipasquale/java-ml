package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextGeneralSupport implements Context.GeneralSupport {
    private DefaultContextGeneralParameters params;

    public static DefaultContextGeneralSupport create(final int populationSize) {
        DefaultContextGeneralParameters params = DefaultContextGeneralParameters.builder()
                .populationSize(populationSize)
                .build();

        return new DefaultContextGeneralSupport(params);
    }

    @Override
    public Context.GeneralParams params() {
        return params;
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("general.params", params);
    }

    public void load(final SerializableStateGroup stateGroup) {
        params = stateGroup.get("general.params");
    }
}
