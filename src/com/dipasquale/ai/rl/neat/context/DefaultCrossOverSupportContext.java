package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultCrossOverSupportContext implements Context.CrossOverSupport {
    private ObjectSwitcher<GateProvider> shouldMateAndMutate;
    private ObjectSwitcher<GateProvider> shouldMateOnly;
    private ObjectSwitcher<GateProvider> shouldMutateOnly;
    private ObjectSwitcher<GateProvider> shouldOverrideConnectionExpressed;
    private ObjectSwitcher<GateProvider> shouldUseRandomParentConnectionWeight;

    @Override
    public boolean shouldMateAndMutate() {
        return shouldMateAndMutate.getObject().isOn();
    }

    @Override
    public boolean shouldMateOnly() {
        return shouldMateOnly.getObject().isOn();
    }

    @Override
    public boolean shouldMutateOnly() {
        return shouldMutateOnly.getObject().isOn();
    }

    @Override
    public boolean shouldOverrideConnectionExpressed() {
        return shouldOverrideConnectionExpressed.getObject().isOn();
    }

    @Override
    public boolean shouldUseRandomParentConnectionWeight() {
        return shouldUseRandomParentConnectionWeight.getObject().isOn();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("crossOver.shouldMateAndMutate", shouldMateAndMutate);
        state.put("crossOver.shouldMateOnly", shouldMateOnly);
        state.put("crossOver.shouldMutateOnly", shouldMutateOnly);
        state.put("crossOver.shouldOverrideConnectionExpressed", shouldOverrideConnectionExpressed);
        state.put("crossOver.shouldUseRandomParentConnectionWeight", shouldUseRandomParentConnectionWeight);
    }

    public void load(final SerializableInteroperableStateMap state, final EventLoopIterable eventLoop) {
        shouldMateAndMutate = ObjectSwitcher.switchObject(state.get("crossOver.shouldMateAndMutate"), eventLoop != null);
        shouldMateOnly = ObjectSwitcher.switchObject(state.get("crossOver.shouldMateOnly"), eventLoop != null);
        shouldMutateOnly = ObjectSwitcher.switchObject(state.get("crossOver.shouldMutateOnly"), eventLoop != null);
        shouldOverrideConnectionExpressed = ObjectSwitcher.switchObject(state.get("crossOver.shouldOverrideConnectionExpressed"), eventLoop != null);
        shouldUseRandomParentConnectionWeight = ObjectSwitcher.switchObject(state.get("crossOver.shouldUseRandomParentConnectionWeight"), eventLoop != null);
    }
}
