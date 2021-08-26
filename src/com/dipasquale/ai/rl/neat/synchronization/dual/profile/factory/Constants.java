package com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.function.activation.ReLUActivationFunction;
import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.StepActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.random.float1.DefaultRandomSupport;
import com.dipasquale.common.random.float1.MeanDistributedRandomSupport;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.ThreadLocalRandomSupport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

final class Constants {
    private static final RandomSupport RANDOM_SUPPORT_UNIFORM_CONCURRENT = new ThreadLocalRandomSupport();
    private static final RandomSupport RANDOM_SUPPORT_UNIFORM = new DefaultRandomSupport();
    private static final RandomSupport RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = new MeanDistributedRandomSupport(RANDOM_SUPPORT_UNIFORM_CONCURRENT, 5);
    private static final RandomSupport RANDOM_SUPPORT_MEAN_DISTRIBUTED = new MeanDistributedRandomSupport(RANDOM_SUPPORT_UNIFORM, 5);

    private static final Map<ActivationFunctionType, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<ActivationFunctionType, ActivationFunction>builder()
            .put(ActivationFunctionType.IDENTITY, IdentityActivationFunction.getInstance())
            .put(ActivationFunctionType.RE_LU, ReLUActivationFunction.getInstance())
            .put(ActivationFunctionType.SIGMOID, SigmoidActivationFunction.getInstance())
            .put(ActivationFunctionType.TAN_H, TanHActivationFunction.getInstance())
            .put(ActivationFunctionType.STEP, StepActivationFunction.getInstance())
            .build();

    private static final List<ActivationFunction> ACTIVATION_FUNCTIONS = ImmutableList.copyOf(ACTIVATION_FUNCTIONS_MAP.values());

    @Generated
    private Constants() {
    }

    private static RandomSupport getRandomSupport(final RandomType type, final boolean concurrent) {
        if (!concurrent) {
            return switch (type) {
                case UNIFORM -> RANDOM_SUPPORT_UNIFORM;

                case MEAN_DISTRIBUTED -> RANDOM_SUPPORT_MEAN_DISTRIBUTED;
            };
        }

        return switch (type) {
            case UNIFORM -> RANDOM_SUPPORT_UNIFORM_CONCURRENT;

            case MEAN_DISTRIBUTED -> RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT;
        };
    }

    static RandomSupport createRandomSupport(final RandomType type, final boolean concurrent) {
        return new ProxyRandomSupport(type, concurrent);
    }

    static ActivationFunction getActivationFunction(final ActivationFunctionType activationFunctionType, final RandomType randomType, final boolean concurrent) {
        return switch (activationFunctionType) {
            case RANDOM -> {
                RandomSupport randomSupport = getRandomSupport(randomType, concurrent);
                int index = randomSupport.next(0, ACTIVATION_FUNCTIONS.size());

                yield ACTIVATION_FUNCTIONS.get(index);
            }

            default -> ACTIVATION_FUNCTIONS_MAP.get(activationFunctionType);
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ProxyRandomSupport implements RandomSupport, Serializable {
        @Serial
        private static final long serialVersionUID = 6745977946618588154L;
        private final RandomType type;
        private final boolean concurrent;

        @Override
        public float next() {
            return getRandomSupport(type, concurrent).next();
        }
    }
}
