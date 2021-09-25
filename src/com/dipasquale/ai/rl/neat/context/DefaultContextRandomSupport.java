package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.RandomSupportFactoryProfile;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class DefaultContextRandomSupport implements Context.RandomSupport {
    private ObjectProfile<RandomSupport> integerRandomSupportProfile;
    private ObjectProfile<RandomSupport> floatRandomSupportProfile;

    public static DefaultContextRandomSupport create(final ParallelismSupport parallelismSupport, final com.dipasquale.ai.rl.neat.settings.RandomSupport randomSupport) {
        ObjectProfile<RandomSupport> integerRandomSupportProfile = new RandomSupportFactoryProfile(parallelismSupport.isEnabled(), randomSupport.getIntegerGenerator());
        ObjectProfile<RandomSupport> floatRandomSupportProfile = new RandomSupportFactoryProfile(parallelismSupport.isEnabled(), randomSupport.getFloatGenerator());

        return new DefaultContextRandomSupport(integerRandomSupportProfile, floatRandomSupportProfile);
    }

    @Override
    public int generateIndex(final int offset, final int count) {
        return integerRandomSupportProfile.getObject().next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return floatRandomSupportProfile.getObject().isLessThan(rate);
    }

    @Override
    public <T> T generateItem(final OutputClassifier<T> outputClassifier) {
        return outputClassifier.resolve(floatRandomSupportProfile.getObject().next());
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("random.integerRandomSupportProfile", integerRandomSupportProfile);
        stateGroup.put("random.floatRandomSupportProfile", floatRandomSupportProfile);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        integerRandomSupportProfile = ObjectProfile.switchProfile(stateGroup.get("random.integerRandomSupportProfile"), eventLoop != null);
        floatRandomSupportProfile = ObjectProfile.switchProfile(stateGroup.get("random.floatRandomSupportProfile"), eventLoop != null);
    }
}
