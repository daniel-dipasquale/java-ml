package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.settings.RandomSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomSupportFactory;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class DefaultContextRandomSupport implements Context.RandomSupport {
    private Map<RandomType, DualModeRandomSupport> randomSupports;
    private DualModeRandomSupport randomSupport;

    private static Map<RandomType, DualModeRandomSupport> createRandomSupports(final ParallelismSupport parallelismSupport) {
        DualModeRandomSupportFactory randomSupportFactory = DualModeRandomSupportFactory.getInstance();
        int concurrencyLevel = parallelismSupport.getConcurrencyLevel();
        Map<RandomType, DualModeRandomSupport> randomSupports = new EnumMap<>(RandomType.class);

        for (RandomType type : RandomType.values()) {
            randomSupports.put(type, randomSupportFactory.create(concurrencyLevel, type));
        }

        return Collections.unmodifiableMap(randomSupports);
    }

    public static DefaultContextRandomSupport create(final ParallelismSupport parallelismSupport, final RandomSupport randomSupport) {
        Map<RandomType, DualModeRandomSupport> randomSupports = createRandomSupports(parallelismSupport);

        return new DefaultContextRandomSupport(randomSupports, randomSupports.get(randomSupport.getType()));
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
