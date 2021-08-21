package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.WeightMutationType;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.profile.ObjectProfile;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextMutationSupport implements Context.MutationSupport {
    private ObjectProfile<GateProvider> shouldAddNodeMutation;
    private ObjectProfile<GateProvider> shouldAddConnectionMutation;
    private ObjectProfile<ObjectFactory<WeightMutationType>> randomWeightMutationTypeGenerator;
    private ObjectProfile<GateProvider> shouldDisableConnectionExpressed;

    @Override
    public boolean shouldAddNodeMutation() {
        return shouldAddNodeMutation.getObject().isOn();
    }

    @Override
    public boolean shouldAddConnectionMutation() {
        return shouldAddConnectionMutation.getObject().isOn();
    }

    @Override
    public WeightMutationType nextWeightMutationType() {
        return randomWeightMutationTypeGenerator.getObject().create();
    }

    @Override
    public boolean shouldDisableConnectionExpressed() {
        return shouldDisableConnectionExpressed.getObject().isOn();
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("mutation.shouldAddNodeMutation", shouldAddNodeMutation);
        state.put("mutation.shouldAddConnectionMutation", shouldAddConnectionMutation);
        state.put("mutation.randomWeightMutationTypeGenerator", randomWeightMutationTypeGenerator);
        state.put("mutation.shouldDisableConnectionExpressed", shouldDisableConnectionExpressed);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        shouldAddNodeMutation = ObjectProfile.switchProfile(state.get("mutation.shouldAddNodeMutation"), eventLoop != null);
        shouldAddConnectionMutation = ObjectProfile.switchProfile(state.get("mutation.shouldAddConnectionMutation"), eventLoop != null);
        randomWeightMutationTypeGenerator = ObjectProfile.switchProfile(state.get("mutation.randomWeightMutationTypeGenerator"), eventLoop != null);
        shouldDisableConnectionExpressed = ObjectProfile.switchProfile(state.get("mutation.shouldDisableConnectionExpressed"), eventLoop != null);
    }
}
