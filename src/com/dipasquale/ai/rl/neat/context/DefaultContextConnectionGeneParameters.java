package com.dipasquale.ai.rl.neat.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DefaultContextConnectionGeneParameters implements Context.ConnectionGeneParameters, Serializable {
    @Serial
    private static final long serialVersionUID = 525702976343792804L;
    private final boolean multipleRecurrentCyclesAllowed;

    @Override
    public boolean multipleRecurrentCyclesAllowed() {
        return multipleRecurrentCyclesAllowed;
    }
}
