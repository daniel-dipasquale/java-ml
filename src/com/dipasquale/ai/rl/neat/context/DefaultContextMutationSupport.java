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
    private ObjectProfile<GateProvider> shouldAddNodeMutationProfile;
    private ObjectProfile<GateProvider> shouldAddConnectionMutationProfile;
    private ObjectProfile<ObjectFactory<WeightMutationType>> weightMutationTypeFactoryProfile;
    private ObjectProfile<GateProvider> shouldDisableExpressedProfile;

    @Override
    public boolean shouldAddNodeMutation() {
        return shouldAddNodeMutationProfile.getObject().isOn();
    }

    @Override
    public boolean shouldAddConnectionMutation() {
        return shouldAddConnectionMutationProfile.getObject().isOn();
    }

    @Override
    public WeightMutationType generateWeightMutationType() {
        return weightMutationTypeFactoryProfile.getObject().create();
    }

    @Override
    public boolean shouldDisableExpressed() {
        return shouldDisableExpressedProfile.getObject().isOn();
    }

    public void save(final SerializableStateGroup state) {
        state.put("mutation.shouldAddNodeMutationProfile", shouldAddNodeMutationProfile);
        state.put("mutation.shouldAddConnectionMutationProfile", shouldAddConnectionMutationProfile);
        state.put("mutation.weightMutationTypeFactoryProfile", weightMutationTypeFactoryProfile);
        state.put("mutation.shouldDisableExpressedProfile", shouldDisableExpressedProfile);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        shouldAddNodeMutationProfile = ObjectProfile.switchProfile(state.get("mutation.shouldAddNodeMutationProfile"), eventLoop != null);
        shouldAddConnectionMutationProfile = ObjectProfile.switchProfile(state.get("mutation.shouldAddConnectionMutationProfile"), eventLoop != null);
        weightMutationTypeFactoryProfile = ObjectProfile.switchProfile(state.get("mutation.weightMutationTypeFactoryProfile"), eventLoop != null);
        shouldDisableExpressedProfile = ObjectProfile.switchProfile(state.get("mutation.shouldDisableExpressedProfile"), eventLoop != null);
    }
}
