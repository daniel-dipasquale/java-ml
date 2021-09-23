package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextCrossOverSupport implements Context.CrossOverSupport {
    private ObjectProfile<GateProvider> shouldOverrideExpressedConnectionProfile;
    private ObjectProfile<GateProvider> shouldUseWeightFromRandomParentProfile;

    @Override
    public boolean shouldOverrideExpressedConnection() {
        return shouldOverrideExpressedConnectionProfile.getObject().isOn();
    }

    @Override
    public boolean shouldUseWeightFromRandomParent() {
        return shouldUseWeightFromRandomParentProfile.getObject().isOn();
    }

    public void save(final SerializableStateGroup state) {
        state.put("crossOver.shouldOverrideExpressedConnectionProfile", shouldOverrideExpressedConnectionProfile);
        state.put("crossOver.shouldUseWeightFromRandomParentProfile", shouldUseWeightFromRandomParentProfile);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        shouldOverrideExpressedConnectionProfile = ObjectProfile.switchProfile(state.get("crossOver.shouldOverrideExpressedConnectionProfile"), eventLoop != null);
        shouldUseWeightFromRandomParentProfile = ObjectProfile.switchProfile(state.get("crossOver.shouldUseWeightFromRandomParentProfile"), eventLoop != null);
    }
}
