package com.dipasquale.ai.rl.neat.switcher;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionIdentity;
import com.dipasquale.ai.common.function.activation.ActivationFunctionReLU;
import com.dipasquale.ai.common.function.activation.ActivationFunctionSigmoid;
import com.dipasquale.ai.common.function.activation.ActivationFunctionStep;
import com.dipasquale.ai.common.function.activation.ActivationFunctionTanH;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.random.float1.DefaultRandomSupport;
import com.dipasquale.common.random.float1.MeanDistributedRandomSupport;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.ThreadLocalRandomSupport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Constants {
    private static final RandomSupport RANDOM_SUPPORT_UNIFORM_CONCURRENT = new ThreadLocalRandomSupport();
    private static final RandomSupport RANDOM_SUPPORT_UNIFORM = new DefaultRandomSupport();
    private static final RandomSupport RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = new MeanDistributedRandomSupport(RANDOM_SUPPORT_UNIFORM_CONCURRENT, 5);
    private static final RandomSupport RANDOM_SUPPORT_MEAN_DISTRIBUTED = new MeanDistributedRandomSupport(RANDOM_SUPPORT_UNIFORM, 5);

    private static final Map<ActivationFunctionType, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<ActivationFunctionType, ActivationFunction>builder()
            .put(ActivationFunctionType.IDENTITY, ActivationFunctionIdentity.getInstance())
            .put(ActivationFunctionType.RE_LU, ActivationFunctionReLU.getInstance())
            .put(ActivationFunctionType.SIGMOID, ActivationFunctionSigmoid.getInstance())
            .put(ActivationFunctionType.TAN_H, ActivationFunctionTanH.getInstance())
            .put(ActivationFunctionType.STEP, ActivationFunctionStep.getInstance())
            .build();

    private static final List<ActivationFunction> ACTIVATION_FUNCTIONS = ImmutableList.copyOf(ACTIVATION_FUNCTIONS_MAP.values());

    static RandomSupport getRandomSupport(final RandomType type, final boolean contended) {
        if (!contended) {
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

    static ActivationFunction getActivationFunction(final ActivationFunctionType activationFunctionType, final RandomType randomType, final boolean contended) {
        return switch (activationFunctionType) {
            case RANDOM -> {
                RandomSupport randomSupport = getRandomSupport(randomType, contended);
                int index = randomSupport.next(0, ACTIVATION_FUNCTIONS.size());

                yield ACTIVATION_FUNCTIONS.get(index);
            }

            default -> ACTIVATION_FUNCTIONS_MAP.get(activationFunctionType);
        };
    }
}
