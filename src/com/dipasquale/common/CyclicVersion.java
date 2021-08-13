/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class CyclicVersion implements Serializable {
    @Serial
    private static final long serialVersionUID = 5427930261018368922L;
    private int value = 0;
    private final int offset;
    private final int max;

    public int next() {
        int next = (offset + value + 1) % max;

        value = next - offset;

        return next;
    }

    public int current() {
        return value + offset;
    }
}
