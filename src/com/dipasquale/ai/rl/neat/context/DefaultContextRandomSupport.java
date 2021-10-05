package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomSupportFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class DefaultContextRandomSupport implements Context.RandomSupport {
    private DualModeRandomSupport randomSupport;
    private Map<RandomType, DualModeRandomSupport> randomSupports;

    private static Map<RandomType, DualModeRandomSupport> createRandomSupports(final ParallelismSupport parallelismSupport, final DualModeRandomSupportFactory randomSupportFactory) {
        int concurrencyLevel = parallelismSupport.getConcurrencyLevel();

        ImmutableMap.Builder<RandomType, DualModeRandomSupport> randomSupportsBuilder = ImmutableMap.builder();

        for (RandomType type : RandomType.values()) {
            randomSupportsBuilder.put(type, randomSupportFactory.create(concurrencyLevel, type));
        }

        return randomSupportsBuilder.build();
    }

    public static DefaultContextRandomSupport create(final ParallelismSupport parallelismSupport, final com.dipasquale.ai.rl.neat.settings.RandomSupport randomSupport) {
        DualModeRandomSupportFactory randomSupportFactory = DualModeRandomSupportFactory.getInstance();
        DualModeRandomSupport randomSupportFixed = randomSupportFactory.create(parallelismSupport.getConcurrencyLevel(), randomSupport.getType());
        Map<RandomType, DualModeRandomSupport> randomSupports = createRandomSupports(parallelismSupport, randomSupportFactory);

        return new DefaultContextRandomSupport(randomSupportFixed, randomSupports);
    }

    @Override
    public int generateIndex(final int offset, final int count) {
        return randomSupport.next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return randomSupport.isLessThan(rate);
    }

    @Override
    public <T> T generateItem(final OutputClassifier<T> outputClassifier) {
        return outputClassifier.resolve(randomSupport.next());
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("random.randomSupport", randomSupport);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        randomSupport = DualModeObject.activateMode(stateGroup.get("random.randomSupport"), ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
