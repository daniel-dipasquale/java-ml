package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.serialization.SerializableStateGroup;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextGeneralSupport implements Context.GeneralSupport {
    private DefaultContextGeneralParameters params;

    @Override
    public Context.GeneralParams params() {
        return params;
    }

    public void save(final SerializableStateGroup state) {
        state.put("general.params", params);
    }

    public void load(final SerializableStateGroup state) {
        params = state.get("general.params");
    }
}
