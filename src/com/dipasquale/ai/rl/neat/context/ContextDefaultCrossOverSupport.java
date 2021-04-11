package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.GateProvider;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public void load(final SerializableInteroperableStateMap state) {
        shouldMateAndMutate = state.get("crossOver.shouldMateAndMutate");
        shouldMateOnly = state.get("crossOver.shouldMateOnly");
        shouldMutateOnly = state.get("crossOver.shouldMutateOnly");
        shouldOverrideConnectionExpressed = state.get("crossOver.shouldOverrideConnectionExpressed");
        shouldUseRandomParentConnectionWeight = state.get("crossOver.shouldUseRandomParentConnectionWeight");
    }
}
