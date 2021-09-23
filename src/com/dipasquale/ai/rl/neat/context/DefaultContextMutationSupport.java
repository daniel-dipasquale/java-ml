package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.common.WeightMutationType;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextMutationSupport implements Context.MutationSupport {
    private ObjectProfile<GateProvider> shouldAddNodeProfile;
    private ObjectProfile<GateProvider> shouldAddConnectionProfile;
    private ObjectProfile<ObjectFactory<WeightMutationType>> weightMutationTypeFactoryProfile;
    private ObjectProfile<GateProvider> shouldDisableExpressedConnectionProfile;

    @Override
    public boolean shouldAddNode() {
        return shouldAddNodeProfile.getObject().isOn();
    }

    @Override
    public boolean shouldAddConnection() {
        return shouldAddConnectionProfile.getObject().isOn();
    }

    @Override
    public WeightMutationType generateWeightMutationType() {
        return weightMutationTypeFactoryProfile.getObject().create();
    }

    @Override
    public boolean shouldDisableExpressedConnection() {
        return shouldDisableExpressedConnectionProfile.getObject().isOn();
    }

    public void save(final SerializableStateGroup state) {
        state.put("mutation.shouldAddNodeProfile", shouldAddNodeProfile);
        state.put("mutation.shouldAddConnectionProfile", shouldAddConnectionProfile);
        state.put("mutation.weightMutationTypeFactoryProfile", weightMutationTypeFactoryProfile);
        state.put("mutation.shouldDisableExpressedConnectionProfile", shouldDisableExpressedConnectionProfile);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        shouldAddNodeProfile = ObjectProfile.switchProfile(state.get("mutation.shouldAddNodeProfile"), eventLoop != null);
        shouldAddConnectionProfile = ObjectProfile.switchProfile(state.get("mutation.shouldAddConnectionProfile"), eventLoop != null);
        weightMutationTypeFactoryProfile = ObjectProfile.switchProfile(state.get("mutation.weightMutationTypeFactoryProfile"), eventLoop != null);
        shouldDisableExpressedConnectionProfile = ObjectProfile.switchProfile(state.get("mutation.shouldDisableExpressedConnectionProfile"), eventLoop != null);
    }
}
