package com.dipasquale.ai.rl.neat;

import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectGeneralSupport implements Context.GeneralSupport {
    private ContextObjectGeneralParameters params;

    static ContextObjectGeneralSupport create(final int populationSize) {
        ContextObjectGeneralParameters params = ContextObjectGeneralParameters.builder()
                .populationSize(populationSize)
                .build();

        return new ContextObjectGeneralSupport(params);
    }

    @Override
    public Context.GeneralParams params() {
        return params;
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("general.params", params);
    }

    static ContextObjectGeneralSupport create(final SerializableStateGroup stateGroup) {
        ContextObjectGeneralParameters params = stateGroup.get("general.params");

        return new ContextObjectGeneralSupport(params);
    }
}
