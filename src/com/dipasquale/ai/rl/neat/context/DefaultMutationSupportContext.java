/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultMutationSupportContext implements Context.MutationSupport {
    private ObjectSwitcher<GateProvider> shouldAddNodeMutation;
    private ObjectSwitcher<GateProvider> shouldAddConnectionMutation;
    private ObjectSwitcher<GateProvider> shouldPerturbConnectionWeight;
    private ObjectSwitcher<GateProvider> shouldReplaceConnectionWeight;
    private ObjectSwitcher<GateProvider> shouldDisableConnectionExpressed;

    @Override
    public boolean shouldAddNodeMutation() {
        return shouldAddNodeMutation.getObject().isOn();
    }

    @Override
    public boolean shouldAddConnectionMutation() {
        return shouldAddConnectionMutation.getObject().isOn();
    }

    @Override
    public boolean shouldPerturbConnectionWeight() {
        return shouldPerturbConnectionWeight.getObject().isOn();
    }

    @Override
    public boolean shouldReplaceConnectionWeight() {
        return shouldReplaceConnectionWeight.getObject().isOn();
    }

    @Override
    public boolean shouldDisableConnectionExpressed() {
        return shouldDisableConnectionExpressed.getObject().isOn();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("mutation.shouldAddNodeMutation", shouldAddNodeMutation);
        state.put("mutation.shouldAddConnectionMutation", shouldAddConnectionMutation);
        state.put("mutation.shouldPerturbConnectionWeight", shouldPerturbConnectionWeight);
        state.put("mutation.shouldReplaceConnectionWeight", shouldReplaceConnectionWeight);
        state.put("mutation.shouldDisableConnectionExpressed", shouldDisableConnectionExpressed);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        shouldAddNodeMutation = ObjectSwitcher.switchObject(state.get("mutation.shouldAddNodeMutation"), eventLoop != null);
        shouldAddConnectionMutation = ObjectSwitcher.switchObject(state.get("mutation.shouldAddConnectionMutation"), eventLoop != null);
        shouldPerturbConnectionWeight = ObjectSwitcher.switchObject(state.get("mutation.shouldPerturbConnectionWeight"), eventLoop != null);
        shouldReplaceConnectionWeight = ObjectSwitcher.switchObject(state.get("mutation.shouldReplaceConnectionWeight"), eventLoop != null);
        shouldDisableConnectionExpressed = ObjectSwitcher.switchObject(state.get("mutation.shouldDisableConnectionExpressed"), eventLoop != null);
    }
}
