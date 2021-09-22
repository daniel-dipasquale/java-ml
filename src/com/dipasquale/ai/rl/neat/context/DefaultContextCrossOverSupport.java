package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextCrossOverSupport implements Context.CrossOverSupport {
    private ObjectProfile<GateProvider> shouldOverrideExpressed;
    private ObjectProfile<GateProvider> shouldUseRandomParentWeight;

    @Override
    public boolean shouldOverrideExpressed() {
        return shouldOverrideExpressed.getObject().isOn();
    }

    @Override
    public boolean shouldUseRandomParentWeight() {
        return shouldUseRandomParentWeight.getObject().isOn();
    }

    public void save(final SerializableStateGroup state) {
        state.put("crossOver.shouldOverrideExpressed", shouldOverrideExpressed);
        state.put("crossOver.shouldUseRandomParentWeight", shouldUseRandomParentWeight);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        shouldOverrideExpressed = ObjectProfile.switchProfile(state.get("crossOver.shouldOverrideExpressed"), eventLoop != null);
        shouldUseRandomParentWeight = ObjectProfile.switchProfile(state.get("crossOver.shouldUseRandomParentWeight"), eventLoop != null);
    }
}
