package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionIdentity;
import com.dipasquale.ai.common.ActivationFunctionReLU;
import com.dipasquale.ai.common.ActivationFunctionSigmoid;
import com.dipasquale.ai.common.ActivationFunctionStep;
import com.dipasquale.ai.common.ActivationFunctionTanH;
import com.dipasquale.common.RandomSupportFloat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SettingsConstants {
    private static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM = RandomSupportFloat.create(false);
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED = RandomSupportFloat.createMeanDistribution(false);
    private static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM_CONCURRENT = RandomSupportFloat.create(true);
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = RandomSupportFloat.createMeanDistribution(true);

    private static final Map<SettingsActivationFunction, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<SettingsActivationFunction, ActivationFunction>builder()
            .put(SettingsActivationFunction.IDENTITY, ActivationFunctionIdentity.getInstance())
            .put(SettingsActivationFunction.RE_LU, ActivationFunctionReLU.getInstance())
            .put(SettingsActivationFunction.SIGMOID, ActivationFunctionSigmoid.getInstance())
            .put(SettingsActivationFunction.TAN_H, ActivationFunctionTanH.getInstance())
            .put(SettingsActivationFunction.STEP, ActivationFunctionStep.getInstance())
            .build();

    private static final List<ActivationFunction> ACTIVATION_FUNCTIONS = ImmutableList.copyOf(ACTIVATION_FUNCTIONS_MAP.values());

    static RandomSupportFloat getRandomSupport(final SettingsRandomType type, final boolean contended) {
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

    static ActivationFunction getActivationFunction(final SettingsActivationFunction activationFunction, final boolean contended) {
        return switch (activationFunction) {
            case RANDOM -> {
                RandomSupportFloat randomSupport = getRandomSupport(SettingsRandomType.UNIFORM, contended);
                int index = randomSupport.next(0, ACTIVATION_FUNCTIONS.size());

                yield ACTIVATION_FUNCTIONS.get(index);
            }

            default -> ACTIVATION_FUNCTIONS_MAP.get(activationFunction);
        };
    }
}
