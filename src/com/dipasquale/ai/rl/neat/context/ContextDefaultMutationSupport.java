package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.GateProvider;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ContextDefaultMutationSupport implements Context.MutationSupport {
    private GateProvider shouldAddNodeMutation;
    private GateProvider shouldAddConnectionMutation;
    private GateProvider shouldPerturbConnectionWeight;
    private GateProvider shouldReplaceConnectionWeight;
    private GateProvider shouldDisableConnectionExpressed;

    @Override
    public boolean shouldAddNodeMutation() {
        return shouldAddNodeMutation.isOn();
    }

    @Override
    public boolean shouldAddConnectionMutation() {
        return shouldAddConnectionMutation.isOn();
    }

    @Override
    public boolean shouldPerturbConnectionWeight() {
        return shouldPerturbConnectionWeight.isOn();
    }

    @Override
    public boolean shouldReplaceConnectionWeight() {
        return shouldReplaceConnectionWeight.isOn();
    }

    @Override
    public boolean shouldDisableConnectionExpressed() {
        return shouldDisableConnectionExpressed.isOn();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("mutation.shouldAddNodeMutation", shouldAddNodeMutation);
        state.put("mutation.shouldAddConnectionMutation", shouldAddConnectionMutation);
        state.put("mutation.shouldPerturbConnectionWeight", shouldPerturbConnectionWeight);
        state.put("mutation.shouldReplaceConnectionWeight", shouldReplaceConnectionWeight);
        state.put("mutation.shouldDisableConnectionExpressed", shouldDisableConnectionExpressed);
    }

    public void load(final SerializableInteroperableStateMap state) {
        shouldAddNodeMutation = state.get("mutation.shouldAddNodeMutation");
        shouldAddConnectionMutation = state.get("mutation.shouldAddConnectionMutation");
        shouldPerturbConnectionWeight = state.get("mutation.shouldPerturbConnectionWeight");
        shouldReplaceConnectionWeight = state.get("mutation.shouldReplaceConnectionWeight");
        shouldDisableConnectionExpressed = state.get("mutation.shouldDisableConnectionExpressed");
    }
}
