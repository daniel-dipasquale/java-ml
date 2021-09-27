package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.settings.CrossOverSupport;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.common.Pair;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.dual.profile.provider.IsLessThanRandomGateProviderProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextCrossOverSupport implements Context.CrossOverSupport {
    private ObjectProfile<GateProvider> shouldOverrideExpressedConnectionProfile;
    private ObjectProfile<GateProvider> shouldUseWeightFromRandomParentProfile;

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelismSupport, final Pair<RandomSupport> randomSupportPair, final float max) {
        return new IsLessThanRandomGateProviderProfile(parallelismSupport.isEnabled(), randomSupportPair, max);
    }

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelismSupport, final Pair<RandomSupport> randomSupportPair, final FloatNumber maximumNumber) {
        float max = maximumNumber.getSingletonValue(parallelismSupport);

        return createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, max);
    }

    public static DefaultContextCrossOverSupport create(final ParallelismSupport parallelismSupport, final ObjectProfile<RandomSupport> randomSupportProfile, final CrossOverSupport crossOverSupport) {
        Pair<RandomSupport> randomSupportPair = ObjectProfile.deconstruct(randomSupportProfile);
        ObjectProfile<GateProvider> shouldOverrideExpressedConnectionProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, crossOverSupport.getOverrideExpressedConnectionRate());
        ObjectProfile<GateProvider> shouldUseWeightFromRandomParentProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, crossOverSupport.getUseWeightFromRandomParentRate());

        return new DefaultContextCrossOverSupport(shouldOverrideExpressedConnectionProfile, shouldUseWeightFromRandomParentProfile);
    }

    @Override
    public boolean shouldOverrideExpressedConnection() {
        return shouldOverrideExpressedConnectionProfile.getObject().isOn();
    }

    @Override
    public boolean shouldUseWeightFromRandomParent() {
        return shouldUseWeightFromRandomParentProfile.getObject().isOn();
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("crossOver.shouldOverrideExpressedConnectionProfile", shouldOverrideExpressedConnectionProfile);
        stateGroup.put("crossOver.shouldUseWeightFromRandomParentProfile", shouldUseWeightFromRandomParentProfile);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        shouldOverrideExpressedConnectionProfile = ObjectProfile.switchProfile(stateGroup.get("crossOver.shouldOverrideExpressedConnectionProfile"), eventLoop != null);
        shouldUseWeightFromRandomParentProfile = ObjectProfile.switchProfile(stateGroup.get("crossOver.shouldUseWeightFromRandomParentProfile"), eventLoop != null);
    }
}
