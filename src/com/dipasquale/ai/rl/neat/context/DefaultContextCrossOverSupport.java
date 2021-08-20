package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextCrossOverSupport implements Context.CrossOverSupport {
    private ObjectSwitcher<GateProvider> shouldOverrideConnectionExpressed;
    private ObjectSwitcher<GateProvider> shouldUseRandomParentConnectionWeight;

    @Override
    public boolean shouldOverrideConnectionExpressed() {
        return shouldOverrideConnectionExpressed.getObject().isOn();
    }

    @Override
    public boolean shouldUseRandomParentConnectionWeight() {
        return shouldUseRandomParentConnectionWeight.getObject().isOn();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("crossOver.shouldOverrideConnectionExpressed", shouldOverrideConnectionExpressed);
        state.put("crossOver.shouldUseRandomParentConnectionWeight", shouldUseRandomParentConnectionWeight);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        shouldOverrideConnectionExpressed = ObjectSwitcher.switchObject(state.get("crossOver.shouldOverrideConnectionExpressed"), eventLoop != null);
        shouldUseRandomParentConnectionWeight = ObjectSwitcher.switchObject(state.get("crossOver.shouldUseRandomParentConnectionWeight"), eventLoop != null);
    }
}
