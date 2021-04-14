package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.GateBiProvider;
import com.dipasquale.ai.common.GateProvider;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ContextDefaultCrossOverSupport implements Context.CrossOverSupport {
    private GateProvider shouldMateAndMutate;
    private GateProvider shouldMateOnly;
    private GateProvider shouldMutateOnly;
    private GateProvider shouldOverrideConnectionExpressed;
    private GateProvider shouldUseRandomParentConnectionWeight;

    @Override
    public boolean shouldMateAndMutate() {
        return shouldMateAndMutate.isOn();
    }

    @Override
    public boolean shouldMateOnly() {
        return shouldMateOnly.isOn();
    }

    @Override
    public boolean shouldMutateOnly() {
        return shouldMutateOnly.isOn();
    }

    @Override
    public boolean shouldOverrideConnectionExpressed() {
        return shouldOverrideConnectionExpressed.isOn();
    }

    @Override
    public boolean shouldUseRandomParentConnectionWeight() {
        return shouldUseRandomParentConnectionWeight.isOn();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("crossOver.shouldMateAndMutate", shouldMateAndMutate);
        state.put("crossOver.shouldMateOnly", shouldMateOnly);
        state.put("crossOver.shouldMutateOnly", shouldMutateOnly);
        state.put("crossOver.shouldOverrideConnectionExpressed", shouldOverrideConnectionExpressed);
        state.put("crossOver.shouldUseRandomParentConnectionWeight", shouldUseRandomParentConnectionWeight);
    }

    private static GateBiProvider load(final GateBiProvider gateProvider, final EventLoopIterable eventLoop) {
        return gateProvider.selectContended(eventLoop != null);
    }

    public void load(final SerializableInteroperableStateMap state, final EventLoopIterable eventLoop) {
        shouldMateAndMutate = load(state.<GateBiProvider>get("crossOver.shouldMateAndMutate"), eventLoop);
        shouldMateOnly = load(state.<GateBiProvider>get("crossOver.shouldMateOnly"), eventLoop);
        shouldMutateOnly = load(state.<GateBiProvider>get("crossOver.shouldMutateOnly"), eventLoop);
        shouldOverrideConnectionExpressed = load(state.<GateBiProvider>get("crossOver.shouldOverrideConnectionExpressed"), eventLoop);
        shouldUseRandomParentConnectionWeight = load(state.<GateBiProvider>get("crossOver.shouldUseRandomParentConnectionWeight"), eventLoop);
    }
}
