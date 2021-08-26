package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.WeightMutationType;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextMutationSupport implements Context.MutationSupport {
    private ObjectProfile<GateProvider> shouldAddNodeMutation;
    private ObjectProfile<GateProvider> shouldAddConnectionMutation;
    private ObjectProfile<ObjectFactory<WeightMutationType>> weightMutationTypeFactory;
    private ObjectProfile<GateProvider> shouldDisableExpressed;

    @Override
    public boolean shouldAddNodeMutation() {
        return shouldAddNodeMutation.getObject().isOn();
    }

    @Override
    public boolean shouldAddConnectionMutation() {
        return shouldAddConnectionMutation.getObject().isOn();
    }

    @Override
    public WeightMutationType generateWeightMutationType() {
        return weightMutationTypeFactory.getObject().create();
    }

    @Override
    public boolean shouldDisableExpressed() {
        return shouldDisableExpressed.getObject().isOn();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("mutation.shouldAddNodeMutation", shouldAddNodeMutation);
        state.put("mutation.shouldAddConnectionMutation", shouldAddConnectionMutation);
        state.put("mutation.weightMutationTypeFactory", weightMutationTypeFactory);
        state.put("mutation.shouldDisableExpressed", shouldDisableExpressed);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        shouldAddNodeMutation = ObjectProfile.switchProfile(state.get("mutation.shouldAddNodeMutation"), eventLoop != null);
        shouldAddConnectionMutation = ObjectProfile.switchProfile(state.get("mutation.shouldAddConnectionMutation"), eventLoop != null);
        weightMutationTypeFactory = ObjectProfile.switchProfile(state.get("mutation.weightMutationTypeFactory"), eventLoop != null);
        shouldDisableExpressed = ObjectProfile.switchProfile(state.get("mutation.shouldDisableExpressed"), eventLoop != null);
    }
}
