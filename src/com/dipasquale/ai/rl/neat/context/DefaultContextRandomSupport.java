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
    private ObjectProfile<RandomSupport> randomSupportProfile;

    public static DefaultContextRandomSupport create(final ParallelismSupport parallelismSupport, final com.dipasquale.ai.rl.neat.settings.RandomSupport randomSupport) {
        ObjectProfile<RandomSupport> randomSupportProfile = new RandomSupportFactoryProfile(parallelismSupport.isEnabled(), randomSupport.getType());

        return new DefaultContextRandomSupport(randomSupportProfile);
    }

    @Override
    public int generateIndex(final int offset, final int count) {
        return randomSupportProfile.getObject().next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return randomSupportProfile.getObject().isLessThan(rate);
    }

    @Override
    public <T> T generateItem(final OutputClassifier<T> outputClassifier) {
        return outputClassifier.resolve(randomSupportProfile.getObject().next());
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("random.randomSupportProfile", randomSupportProfile);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        randomSupportProfile = ObjectProfile.switchProfile(stateGroup.get("random.randomSupportProfile"), eventLoop != null);
    }
}
