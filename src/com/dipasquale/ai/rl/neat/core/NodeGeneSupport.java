package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextNodeGeneSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class NodeGeneSupport {
    @Builder.Default
    private final FloatNumber inputBias = FloatNumber.literal(0f);
    @Builder.Default
    private final EnumValue<ActivationFunctionType> inputActivationFunction = EnumValue.literal(ActivationFunctionType.IDENTITY);
    @Builder.Default
    private final FloatNumber outputBias = FloatNumber.random(RandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final EnumValue<OutputActivationFunctionType> outputActivationFunction = EnumValue.literal(OutputActivationFunctionType.SIGMOID);
    @Builder.Default
    private final FloatNumber hiddenBias = FloatNumber.random(RandomType.UNIFORM, -1f, 1f);
    @Builder.Default
    private final EnumValue<ActivationFunctionType> hiddenActivationFunction = EnumValue.literal(ActivationFunctionType.TAN_H);

    DefaultContextNodeGeneSupport create(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate) {
        return DefaultContextNodeGeneSupport.create(initializationContext, genesisGenomeTemplate, this);
    }
}
