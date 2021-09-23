package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextCrossOverSupport implements Context.CrossOverSupport {
    private ObjectProfile<GateProvider> shouldOverrideExpressedProfile;
    private ObjectProfile<GateProvider> shouldUseRandomParentWeightProfile;

    @Override
    public boolean shouldOverrideExpressed() {
        return shouldOverrideExpressedProfile.getObject().isOn();
    }

    @Override
    public boolean shouldUseRandomParentWeight() {
        return shouldUseRandomParentWeightProfile.getObject().isOn();
    }

    public void save(final SerializableStateGroup state) {
        state.put("crossOver.shouldOverrideExpressedProfile", shouldOverrideExpressedProfile);
        state.put("crossOver.shouldUseRandomParentWeightProfile", shouldUseRandomParentWeightProfile);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        shouldOverrideExpressedProfile = ObjectProfile.switchProfile(state.get("crossOver.shouldOverrideExpressedProfile"), eventLoop != null);
        shouldUseRandomParentWeightProfile = ObjectProfile.switchProfile(state.get("crossOver.shouldUseRandomParentWeightProfile"), eventLoop != null);
    }
}
