package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class NodeGeneSupport {
    @Builder.Default
    private final FloatNumber inputBias = FloatNumber.literal(0f);
    @Builder.Default
    private final EnumValue<ActivationFunctionType> inputActivationFunction = EnumValue.literal(ActivationFunctionType.IDENTITY);
    @Builder.Default
    private final FloatNumber outputBias = FloatNumber.random(RandomType.QUADRUPLE_SIGMOID, 15f);
    @Builder.Default
    private final EnumValue<OutputActivationFunctionType> outputActivationFunction = EnumValue.literal(OutputActivationFunctionType.SIGMOID);
    @Builder.Default
    private final FloatNumber hiddenBias = FloatNumber.random(RandomType.QUADRUPLE_STEEPENED_SIGMOID, 30f);
    @Builder.Default
    private final EnumValue<ActivationFunctionType> hiddenActivationFunction = EnumValue.literal(ActivationFunctionType.SIGMOID);

    ContextObjectNodeGeneSupport create(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate, final ConnectionGeneSupport connectionGeneSupport) {
        return ContextObjectNodeGeneSupport.create(initializationContext, genesisGenomeTemplate, this, connectionGeneSupport);
    }
}
